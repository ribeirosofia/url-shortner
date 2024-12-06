package com.oopsitshrinked.createUrlShortner;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;


public class Main implements RequestHandler<Map<String, Object>, Map<String, String>> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final S3Client s3Client = S3Client.builder().build();

    @Override
    public Map<String, String> handleRequest(Map<String, Object> input, Context context) {
        String body = (String) input.get("body");

        if (body == null) {
            throw new IllegalArgumentException("Request body is missing");
        }

        Map<String, String> bodyMap;
        try{
            bodyMap = objectMapper.readValue(body, Map.class);
        } catch(Exception e){
            throw new RuntimeException("Error parsing JSON body: " + e.getMessage(), e);
        }

        String originalUrl = bodyMap.get("originalUrl");

        if (originalUrl == null || originalUrl.isEmpty()) {
            throw new IllegalArgumentException("The 'originalUrl' field is missing or empty");
        }


        String expirationTime = bodyMap.get("expirationTime");
        if (expirationTime == null || expirationTime.isEmpty()) {
            throw new IllegalArgumentException("The 'expirationTime' field is missing or empty");
        }

        long expirationTimeInSeconds = Long.parseLong(expirationTime) * 3600;

        String shortUrlCode = UUID.randomUUID().toString().substring(0, 8);

        UrlData urlData = new UrlData(originalUrl, expirationTimeInSeconds);

        try{
            String urlDataJson = objectMapper.writeValueAsString(urlData);

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket("url-shortener-sofia-ribeiro-lambda1-2024")
                    .key(shortUrlCode + ".json")
                    .build();

            s3Client.putObject(request, RequestBody.fromString(urlDataJson));
        } catch (Exception exception) {
            context.getLogger().log("Error saving data to S3: " + exception.getMessage());
            throw new RuntimeException("Error saving data to S3", exception);
        }

        Map<String, String> response = new HashMap<>();
        response.put("code", shortUrlCode);
        return response;
    }
}