# mock-service

## Overview
This is the mock server implementation in Java. It supports general purpose mock implementation.
### Service exposes
1. Configure mock endpoint - that can be used to setup a mock Response for any endpoint.
2. Mock API - Actual mock API, that will return configured mock response for requested resource.

## How to Run
1. Download the source code from 
    ```
    git clone https://github.com/kdsiitd/mock-service.git
   
    cd mock-service/src/main/resources
    ```
2. Edit the configuration file set your own configurations.
3. Use the SQL file schema.sql to setup the database schema needed to run this application.
4. Make sure you have Gradle configured and JDK 21 installed.
5. Build the application
```
./gradlew clean build
```
6. Gradle bootrun to start the springboot application on http://localhost:8080
```
./gradlew bootrun
```