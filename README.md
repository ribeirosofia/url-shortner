# Java Project with AWS infrastructure for S3 Lambda

This project was build as a project for the Rocketseat class to learn about building class functions and methods using the AWS cloud.

To initialize the project, the user must install JDK 17 to run the project. Also, it is necessary to install maven dependencies.

To help keep tracking of the updates, build project and encapsulate the JAR, it's necessary to run the following command line:

- mvn clean install

To execute locally, it's possible to use AWS SAM using the following command line:

- sam local invoke CreateUrlShortnerFunction --event event.json
