# Mock Service

## Overview
A flexible and powerful mock server implementation in Java that allows you to create and manage mock endpoints for testing and development purposes. This service provides a simple way to configure and serve mock responses for any HTTP endpoint.

## Features
- Configure mock endpoints with custom responses
- Support for different HTTP methods (GET, POST, PUT, DELETE, etc.)
- Customizable response headers and status codes
- JSON response body support
- Persistent storage of mock configurations
- RESTful API for managing mock endpoints

## Prerequisites
- JDK 21 or higher
- Gradle 8.x or higher
- MySQL 8.0 or higher

## Database Setup
1. Create a MySQL database named `mock_db`
2. Run the schema.sql file located in `src/main/resources/schema.sql` to create the required tables

## Configuration
1. Update the database configuration in `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/mock_db
spring.datasource.username=your_username
spring.datasource.password=your_password
```

## Building and Running
1. Clone the repository:
```bash
git clone https://github.com/kdsiitd/mock-service.git
cd mock-service
```

2. Build the application:
```bash
./gradlew clean build
```

3. Run the application:
```bash
./gradlew bootrun
```

The application will start on http://localhost:8080

## API Documentation

### Configure Mock Endpoint
```http
POST /endpoints/configure
Content-Type: application/json

{
    "path": "/api/users",
    "method": "GET",
    "statusCode": 200,
    "responseHeaders": {
        "Content-Type": "application/json"
    },
    "body": "{\"users\": []}",
    "contentType": "application/json",
    "description": "Mock users endpoint"
}
```

### List Configured Endpoints
```http
GET /endpoints/list
```

### Mock API
Any configured endpoint can be accessed using the configured path. For example:
```http
GET /api/users
```

## Testing
Run the test suite:
```bash
./gradlew test
```

## Contributing
1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License
This project is licensed under the MIT License - see the LICENSE file for details.