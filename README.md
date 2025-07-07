# Mock Service

## Overview
A flexible and powerful mock server implementation in Java that allows you to create and manage mock endpoints for testing and development purposes. This service provides a simple way to configure and serve mock responses for any HTTP endpoint with advanced features like load testing simulation, rate limiting, and realistic latency patterns.

## Features
- **Complete CRUD Operations**: Create, Read, Update, and Delete mock endpoints
- **Multiple HTTP Methods**: Support for GET, POST, PUT, DELETE, PATCH, HEAD, OPTIONS
- **Customizable Responses**: Custom status codes, headers, and response bodies
- **Load Testing Simulation**: Realistic latency patterns, timeouts, and error rates
- **Rate Limiting**: Configurable request rate limiting with burst support
- **Health Monitoring**: Built-in health checks and system monitoring
- **Persistent Storage**: MySQL database for storing mock configurations
- **RESTful API**: Complete REST API for managing mock endpoints
- **Comprehensive Documentation**: Interactive Swagger UI with examples
- **Docker Support**: Complete containerized development environment
- **Advanced Configuration**: JSON and XML response support, file upload simulation

## Prerequisites
- JDK 21 or higher
- Gradle 8.x or higher
- MySQL 8.0 or higher (or Docker for containerized setup)

## Quick Start

### Option 1: Docker Compose (Recommended)
```bash
# Clone the repository
git clone https://github.com/kdsiitd/mock-service.git
cd mock-service

# Start the complete environment
docker-compose up -d

# The service will be available at:
# - Application: http://localhost:8080
# - Swagger UI: http://localhost:8080/swagger-ui.html
# - Health Check: http://localhost:8080/health
```

### Option 2: Local Development
```bash
# Clone the repository
git clone https://github.com/kdsiitd/mock-service.git
cd mock-service

# Set up MySQL database
mysql -u root -p < src/main/resources/schema.sql

# Configure environment variables
export SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/mock_db
export SPRING_DATASOURCE_USERNAME=your_username
export SPRING_DATASOURCE_PASSWORD=your_password

# Build and run
./gradlew clean build
./gradlew bootrun
```

## Database Setup

### Manual Setup
1. Install MySQL 8.0 or higher
2. Create a database:
```sql
CREATE DATABASE mock_db;
```
3. Run the schema initialization:
```bash
mysql -u root -p mock_db < src/main/resources/schema.sql
```

### Docker Setup
The Docker Compose configuration automatically sets up MySQL with:
- Database: `mock_db`
- User: `mock_user`
- Password: `mock_password`
- Port: `3306`

## Configuration

### Environment Variables
| Variable | Default | Description |
|----------|---------|-------------|
| `SPRING_DATASOURCE_URL` | `jdbc:mysql://localhost:3306/mock_db` | Database connection URL |
| `SPRING_DATASOURCE_USERNAME` | `root` | Database username |
| `SPRING_DATASOURCE_PASSWORD` | `password` | Database password |
| `SERVER_PORT` | `8080` | Application port |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | `update` | JPA schema generation mode |
| `LOGGING_LEVEL_COM_KDS_MOCK` | `DEBUG` | Application logging level |

### Application Properties
```properties
# Database Configuration
spring.datasource.url=${SPRING_DATASOURCE_URL:jdbc:mysql://localhost:3306/mock_db}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME:root}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD:password}

# Connection Pool
spring.datasource.hikari.connection-timeout=60000
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5

# JPA Configuration
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
spring.jpa.hibernate.ddl-auto=${SPRING_JPA_HIBERNATE_DDL_AUTO:update}

# Server Configuration
server.port=${SERVER_PORT:8080}

# Logging
logging.level.com.kds.mock=${LOGGING_LEVEL_COM_KDS_MOCK:DEBUG}
```

## API Documentation

### Swagger/OpenAPI Documentation
The Mock Service provides comprehensive API documentation through Swagger UI:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs
- **OpenAPI YAML**: http://localhost:8080/api-docs.yaml

### API Endpoints Overview

#### Endpoint Configuration (`/endpoints`)
- `GET /endpoints` - List all configured endpoints
- `POST /endpoints` - Create a new mock endpoint
- `GET /endpoints/id/{id}` - Get specific endpoint by ID
- `GET /endpoints/{path}?method={method}` - Get specific endpoint by path and method
- `PUT /endpoints/{path}?method={method}` - Update existing endpoint
- `DELETE /endpoints/{path}?method={method}` - Delete endpoint

#### Mock Responses (`/api/**`)
- Dynamic endpoints that serve configured responses
- Supports all HTTP methods
- Paths are determined by your configurations
- Advanced load testing simulation with realistic latency and error patterns

#### Load Testing (`/load-test`)
- `POST /load-test/validate` - Validate load test configuration
- `GET /load-test/stats` - Get load testing statistics
- `POST /load-test/reset` - Reset load testing state

#### Health Check (`/health`)
- `GET /health` - Service health status
- `GET /health/info` - Detailed system information

## Usage Examples

### Basic Endpoint Configuration
```bash
# Create a simple GET endpoint
curl -X POST http://localhost:8080/endpoints \
  -H "Content-Type: application/json" \
  -d '{
    "path": "/api/users",
    "method": "GET",
    "statusCode": 200,
    "responseHeaders": {
      "Content-Type": "application/json",
      "Cache-Control": "no-cache"
    },
    "body": "{\"users\": [{\"id\": 1, \"name\": \"John Doe\", \"email\": \"john@example.com\"}]}",
    "contentType": "application/json",
    "description": "Mock users endpoint"
  }'

# Test the endpoint
curl http://localhost:8080/api/users
```

### Advanced Load Testing Configuration
```bash
# Create endpoint with comprehensive load testing simulation
curl -X POST http://localhost:8080/endpoints \
  -H "Content-Type: application/json" \
  -d '{
    "path": "/api/load-test",
    "method": "GET",
    "statusCode": 200,
    "responseHeaders": {
      "Content-Type": "application/json",
      "X-Response-Time": "150ms"
    },
    "body": "{\"message\": \"Load test response\", \"timestamp\": \"2024-01-15T10:30:00Z\", \"server\": \"mock-01\"}",
    "contentType": "application/json",
    "description": "Load testing endpoint with realistic simulation",
    "loadTestConfig": {
      "latencyConfig": {
        "type": "PERCENTILE",
        "p50": 100,
        "p95": 500,
        "p99": 1000,
        "p999": 2000
      },
      "timeoutConfig": {
        "enabled": true,
        "timeoutMs": 5000,
        "timeoutProbability": 0.01
      },
      "errorConfig": {
        "enabled": true,
        "errorRate": 0.05,
        "errorStatusCodes": [500, 502, 503],
        "errorMessages": {
          "500": "Internal server error during load test",
          "502": "Bad gateway during high load",
          "503": "Service temporarily unavailable"
        }
      },
      "rateLimitConfig": {
        "enabled": true,
        "requestsPerSecond": 100,
        "burstSize": 10,
        "rateLimitStatusCode": 429
      }
    }
  }'
```

### Error Simulation
```bash
# Create an endpoint that returns errors
curl -X POST http://localhost:8080/endpoints \
  -H "Content-Type: application/json" \
  -d '{
    "path": "/api/error",
    "method": "GET",
    "statusCode": 404,
    "responseHeaders": {
      "Content-Type": "application/json"
    },
    "body": "{\"error\": \"Not Found\", \"message\": \"Resource not found\", \"status\": 404}",
    "contentType": "application/json",
    "description": "Error simulation endpoint"
  }'
```

### XML Response Configuration
```bash
# Create an XML endpoint for legacy systems
curl -X POST http://localhost:8080/endpoints \
  -H "Content-Type: application/json" \
  -d '{
    "path": "/api/xml-data",
    "method": "GET",
    "statusCode": 200,
    "responseHeaders": {
      "Content-Type": "application/xml",
      "X-Content-Version": "1.0"
    },
    "body": "<?xml version=\"1.0\" encoding=\"UTF-8\"?><users><user id=\"1\"><name>John Doe</name><email>john@example.com</email></user></users>",
    "contentType": "application/xml",
    "description": "Mock XML endpoint for legacy systems"
  }'
```

### Get Endpoint by ID
```bash
# Get endpoint details by ID
curl http://localhost:8080/endpoints/id/1
```

### Update Existing Endpoint
```bash
# Update an existing endpoint with load testing configuration
curl -X PUT "http://localhost:8080/endpoints/api%2Fusers?method=GET" \
  -H "Content-Type: application/json" \
  -d '{
    "statusCode": 200,
    "body": "{\"users\": [{\"id\": 1, \"name\": \"Updated User\", \"email\": \"updated@example.com\"}]}",
    "description": "Updated mock users endpoint",
    "loadTestConfig": {
      "latencyConfig": {
        "type": "FIXED",
        "fixedLatencyMs": 250
      },
      "errorConfig": {
        "enabled": true,
        "errorRate": 0.1,
        "errorStatusCodes": [503],
        "errorMessages": {
          "503": "Service temporarily unavailable during maintenance"
        }
      }
    }
  }'
```

### Delete Endpoint
```bash
# Delete an endpoint
curl -X DELETE "http://localhost:8080/endpoints/api%2Fusers?method=GET"
```

## Load Testing Features

### Latency Configuration
- **FIXED**: Constant latency for predictable testing
- **PERCENTILE**: Realistic percentile-based latency distribution
- **RANDOM**: Random latency within specified range
- **NORMAL_DISTRIBUTION**: Normal distribution latency for statistical accuracy

### Error Simulation
- Configurable error rates (0.0 to 1.0)
- Custom error status codes (4xx, 5xx)
- Custom error messages per status code
- Realistic error distribution patterns

### Timeout Simulation
- Configurable timeout duration
- Probability-based timeout occurrence
- Realistic timeout behavior for load testing

### Rate Limiting
- Requests per second limits
- Burst size configuration
- Custom rate limit status codes
- Per-endpoint rate limiting

## Response Format

### Standard Success Response
```json
{
  "endpoints": {
    "id": 1,
    "path": "/api/users",
    "statusCode": 200,
    "description": "Mock users endpoint",
    "createdAt": "2024-01-15T10:30:00.000",
    "updatedAt": "2024-01-15T10:30:00.000"
  },
  "headers": [
    {
      "id": 1,
      "name": "Content-Type",
      "value": "application/json",
      "createdAt": "2024-01-15T10:30:00.000",
      "updatedAt": "2024-01-15T10:30:00.000"
    }
  ],
  "responses": {
    "id": 1,
    "method": "GET",
    "contentType": "application/json",
    "body": "{\"users\": [{\"id\": 1, \"name\": \"John Doe\"}]}",
    "createdAt": "2024-01-15T10:30:00.000",
    "updatedAt": "2024-01-15T10:30:00.000"
  },
  "loadTestConfig": {
    "latencyConfig": {
      "type": "PERCENTILE",
      "p50": 100,
      "p95": 500,
      "p99": 1000
    },
    "errorConfig": {
      "enabled": true,
      "errorRate": 0.05
    }
  }
}
```

### Standard Error Response
```json
{
  "error": "Bad Request",
  "message": "Validation failed",
  "status": 400,
  "timestamp": "2024-01-15T10:30:00.000",
  "path": "/endpoints",
  "validationErrors": [
    {
      "field": "statusCode",
      "rejectedValue": 999,
      "message": "Status code must be between 100 and 599"
    }
  ]
}
```

## Validation Rules

### Endpoint Configuration
- **Path**: Must start with `/`, 2-512 characters
- **Method**: Must be valid HTTP method (GET, POST, PUT, DELETE, PATCH, HEAD, OPTIONS)
- **Status Code**: Must be between 100-599
- **Content Type**: Must be valid MIME type format
- **Description**: 5-255 characters
- **Response Body**: Maximum 65,535 characters
- **Headers**: Maximum 50 headers, 1000 characters each

### Load Testing Configuration
- **Latency Values**: Must be non-negative
- **Percentile Values**: Must be in ascending order (P50 ≤ P95 ≤ P99 ≤ P999)
- **Error Rates**: Must be between 0.0 and 1.0
- **Timeout Probability**: Must be between 0.0 and 1.0
- **Rate Limiting**: Must be positive integers

## Error Handling

### Common Error Scenarios
- **400 Bad Request**: Invalid input data, validation errors
- **404 Not Found**: Endpoint not configured
- **409 Conflict**: Duplicate endpoint configuration
- **500 Internal Server Error**: Server-side errors

## Performance Tuning

### Database Optimization
- Use connection pooling (HikariCP)
- Configure appropriate pool sizes
- Monitor query performance
- Use TEXT columns for large JSON data

### JVM Tuning
```bash
# Recommended JVM options
java -Xms512m -Xmx2g -XX:+UseG1GC -jar app.jar
```

### Load Testing Best Practices
- Start with realistic latency patterns
- Gradually increase load
- Monitor system resources
- Use appropriate error rates (typically 1-5%)

## Monitoring and Health Checks

### Health Endpoints
```bash
# Basic health check
curl http://localhost:8080/health

# Detailed system information
curl http://localhost:8080/health/info
```

### Load Testing Statistics
```bash
# Get load testing stats
curl http://localhost:8080/load-test/stats

# Validate load test configuration
curl -X POST http://localhost:8080/load-test/validate \
  -H "Content-Type: application/json" \
  -d '{
    "latencyConfig": {
      "type": "PERCENTILE",
      "p50": 100,
      "p95": 500,
      "p99": 1000
    }
  }'
```

## Development

### Building from Source
```bash
# Clone and build
git clone https://github.com/kdsiitd/mock-service.git
cd mock-service
./gradlew clean build

# Run tests
./gradlew test

# Run with coverage
./gradlew jacocoTestReport
```

### Project Structure
```
src/
├── main/
│   ├── java/com/kds/mock/
│   │   ├── config/          # Configuration classes (OpenAPI, Jackson, JPA, Global Exception Handler)
│   │   ├── controllers/     # REST controllers (MockConfigure, MockRequest, LoadTest, Health)
│   │   ├── dto/            # Data transfer objects (Request/Response DTOs, LoadTestConfig)
│   │   ├── entity/         # JPA entities (Endpoints, Headers, Responses, BaseEntity)
│   │   ├── repository/     # Data access layer (JPA repositories)
│   │   └── service/        # Business logic (Service interfaces and implementations)
│   └── resources/
│       ├── application.properties
│       └── schema.sql      # Database schema initialization
└── test/                   # Comprehensive test classes
    ├── controllers/        # Controller tests
    ├── service/           # Service layer tests
    ├── dto/               # DTO validation tests
    └── entity/            # Entity tests
```

### Testing
```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests MockConfigureControllerTests

# Run with coverage
./gradlew jacocoTestReport

# View coverage report
open build/reports/jacoco/test/html/index.html
```

## Deployment

### Docker Deployment
```bash
# Build image
docker build -t mock-service .

# Run with external MySQL
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://mysql-host:3306/mock_db \
  -e SPRING_DATASOURCE_USERNAME=user \
  -e SPRING_DATASOURCE_PASSWORD=pass \
  mock-service
```

### Production Considerations
- Use external MySQL database with proper backup strategy
- Configure proper logging levels and log rotation
- Set up monitoring and alerting (health endpoints)
- Use reverse proxy (nginx) for SSL termination
- Configure SSL/TLS certificates
- Set appropriate JVM memory settings
- Use connection pooling optimization
- Implement proper security headers

## Troubleshooting

### Common Issues

#### Database Connection Issues
```bash
# Check database connectivity
mysql -h localhost -u mock_user -p mock_db

# Verify environment variables
echo $SPRING_DATASOURCE_URL

# Check connection pool status
curl http://localhost:8080/health/info
```

#### Port Conflicts
```bash
# Check if port is in use
lsof -i :8080

# Use different port
export SERVER_PORT=8081
```

#### Memory Issues
```bash
# Monitor memory usage
jstat -gc <pid>

# Increase heap size
java -Xmx4g -jar app.jar
```

#### Load Testing Issues
```bash
# Validate load test configuration
curl -X POST http://localhost:8080/load-test/validate \
  -H "Content-Type: application/json" \
  -d '{"latencyConfig": {"type": "INVALID"}}'

# Reset load testing state
curl -X POST http://localhost:8080/load-test/reset
```

### Logs
```bash
# View application logs
tail -f logs/application.log

# Docker logs
docker-compose logs -f app

# Check for specific errors
grep -i error logs/application.log
```

## Contributing
1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Development Guidelines
- Follow Java coding conventions
- Add comprehensive tests for new features
- Update documentation (README, Swagger annotations)
- Use meaningful commit messages
- Ensure all tests pass before submitting PR
- Add validation for new configuration options

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support
- **Issues**: [GitHub Issues](https://github.com/kdsiitd/mock-service/issues)
- **Documentation**: [Wiki](https://github.com/kdsiitd/mock-service/wiki)
- **Discussions**: [GitHub Discussions](https://github.com/kdsiitd/mock-service/discussions)

## Changelog
See [CHANGELOG.md](CHANGELOG.md) for a list of changes and version history.

## Acknowledgments
- Spring Boot framework for rapid application development
- Swagger/OpenAPI for comprehensive API documentation
- MySQL for reliable data persistence
- Docker for containerization support
- Lombok for reducing boilerplate code