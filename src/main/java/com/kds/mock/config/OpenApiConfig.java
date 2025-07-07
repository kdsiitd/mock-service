package com.kds.mock.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.Components;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Value("${spring.application.name:mock-service}")
    private String applicationName;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Mock Service API")
                        .description("""
                            # Mock Service API Documentation
                            
                            A flexible and powerful mock server implementation that allows you to create and manage mock endpoints for testing and development purposes.
                            
                            ## Features
                            
                            - **Dynamic Endpoint Configuration**: Create, update, and delete mock endpoints on the fly
                            - **Load Testing Simulation**: Configure realistic latency, error rates, and timeouts
                            - **Flexible Response Configuration**: Support for custom headers, status codes, and response bodies
                            - **Health Monitoring**: Built-in health check endpoints for monitoring
                            - **Comprehensive API Documentation**: Full OpenAPI 3.0 specification with examples
                            
                            ## Getting Started
                            
                            1. **Configure Mock Endpoints**: Use the `/endpoints` API to create your mock configurations
                            2. **Test Your Endpoints**: Make requests to `/api/**` paths to get your configured responses
                            3. **Monitor Health**: Use `/health` endpoints to monitor service status
                            4. **Load Testing**: Configure load testing parameters for realistic simulation
                            
                            ## API Versioning
                            
                            This API follows semantic versioning. Breaking changes will result in a new major version.
                            
                            ## Support
                            
                            For issues and questions, please visit our GitHub repository or contact the development team.
                            """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Mock Service Team")
                                .url("https://github.com/kdsiitd/mock-service")
                                .email("support@mock-service.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Local Development Server"),
                        new Server()
                                .url("https://api.mock-service.com")
                                .description("Production Server"),
                        new Server()
                                .url("https://staging.mock-service.com")
                                .description("Staging Server")))
                .tags(List.of(
                        new Tag()
                                .name("Endpoint Configuration")
                                .description("APIs for configuring and managing mock endpoints. Use these endpoints to create, retrieve, update, and delete mock API configurations."),
                        new Tag()
                                .name("Mock Responses")
                                .description("Dynamic mock endpoints that serve configured responses. These endpoints are created automatically when you configure mock endpoints."),
                        new Tag()
                                .name("Health Check")
                                .description("Health monitoring and system status endpoints for monitoring the mock service and diagnosing issues."),
                        new Tag()
                                .name("Load Testing")
                                .description("APIs for managing and monitoring load testing configurations and performance metrics.")))
                .components(new Components()
                        .responses(createGlobalResponses())
                        .examples(createGlobalExamples())
                        .schemas(createGlobalSchemas()));
    }

    private Map<String, ApiResponse> createGlobalResponses() {
        return Map.of(
                "BadRequest", new ApiResponse()
                        .description("Bad Request - Invalid input data")
                        .content(new Content()
                                .addMediaType("application/json", new MediaType()
                                        .schema(new Schema<>().$ref("#/components/schemas/ErrorResponse"))
                                        .example(Map.of(
                                                "error", "Bad Request",
                                                "message", "Invalid input data",
                                                "code", 400,
                                                "timestamp", "2024-01-15T10:30:00Z",
                                                "path", "/endpoints"
                                        )))),
                "NotFound", new ApiResponse()
                        .description("Not Found - Resource not found")
                        .content(new Content()
                                .addMediaType("application/json", new MediaType()
                                        .schema(new Schema<>().$ref("#/components/schemas/ErrorResponse"))
                                        .example(Map.of(
                                                "error", "Not Found",
                                                "message", "Resource not found",
                                                "code", 404,
                                                "timestamp", "2024-01-15T10:30:00Z",
                                                "path", "/endpoints/api/users"
                                        )))),
                "InternalServerError", new ApiResponse()
                        .description("Internal Server Error - Unexpected server error")
                        .content(new Content()
                                .addMediaType("application/json", new MediaType()
                                        .schema(new Schema<>().$ref("#/components/schemas/ErrorResponse"))
                                        .example(Map.of(
                                                "error", "Internal Server Error",
                                                "message", "An unexpected error occurred",
                                                "code", 500,
                                                "timestamp", "2024-01-15T10:30:00Z",
                                                "path", "/endpoints"
                                        )))),
                "ValidationError", new ApiResponse()
                        .description("Validation Error - Request validation failed")
                        .content(new Content()
                                .addMediaType("application/json", new MediaType()
                                        .schema(new Schema<>().$ref("#/components/schemas/ValidationErrorResponse"))
                                        .example(Map.of(
                                                "error", "Validation Failed",
                                                "message", "Request validation failed",
                                                "code", 400,
                                                "timestamp", "2024-01-15T10:30:00Z",
                                                "path", "/endpoints",
                                                "validationErrors", List.of(
                                                        Map.of("field", "path", "message", "Path is required"),
                                                        Map.of("field", "method", "message", "HTTP method is required")
                                                )
                                        ))))
        );
    }

    private Map<String, Example> createGlobalExamples() {
        return Map.of(
                "SimpleGetEndpoint", new Example()
                        .summary("Simple GET endpoint")
                        .description("Basic GET endpoint returning JSON data")
                        .value(Map.of(
                                "path", "/api/users",
                                "method", "GET",
                                "statusCode", 200,
                                "responseHeaders", Map.of("Content-Type", "application/json"),
                                "body", "{\"users\": [{\"id\": 1, \"name\": \"John Doe\"}]}",
                                "contentType", "application/json",
                                "description", "Mock users endpoint"
                        )),
                "ErrorEndpoint", new Example()
                        .summary("Error response endpoint")
                        .description("Endpoint that returns an error response")
                        .value(Map.of(
                                "path", "/api/error",
                                "method", "GET",
                                "statusCode", 404,
                                "responseHeaders", Map.of("Content-Type", "application/json"),
                                "body", "{\"error\": \"Resource not found\"}",
                                "contentType", "application/json",
                                "description", "Mock error endpoint for testing"
                        )),
                "PostEndpoint", new Example()
                        .summary("POST endpoint")
                        .description("POST endpoint for creating resources")
                        .value(Map.of(
                                "path", "/api/users",
                                "method", "POST",
                                "statusCode", 201,
                                "responseHeaders", Map.of(
                                        "Content-Type", "application/json",
                                        "Location", "/api/users/123"
                                ),
                                "body", "{\"id\": 123, \"name\": \"John Doe\", \"created\": true}",
                                "contentType", "application/json",
                                "description", "Mock user creation endpoint"
                        ))
        );
    }

    private Map<String, Schema> createGlobalSchemas() {
        return Map.of(
                "ErrorResponse", new Schema<>()
                        .type("object")
                        .description("Standard error response format")
                        .addProperty("error", new Schema<>().type("string").description("Error type"))
                        .addProperty("message", new Schema<>().type("string").description("Error message"))
                        .addProperty("code", new Schema<>().type("integer").description("HTTP status code"))
                        .addProperty("timestamp", new Schema<>().type("string").format("date-time").description("Error timestamp"))
                        .addProperty("path", new Schema<>().type("string").description("Request path that caused the error"))
                        .required(List.of("error", "message", "code", "timestamp")),
                "ValidationErrorResponse", new Schema<>()
                        .type("object")
                        .description("Validation error response format")
                        .addProperty("error", new Schema<>().type("string").description("Error type"))
                        .addProperty("message", new Schema<>().type("string").description("Error message"))
                        .addProperty("code", new Schema<>().type("integer").description("HTTP status code"))
                        .addProperty("timestamp", new Schema<>().type("string").format("date-time").description("Error timestamp"))
                        .addProperty("path", new Schema<>().type("string").description("Request path that caused the error"))
                        .addProperty("validationErrors", new Schema<>()
                                .type("array")
                                .description("List of validation errors")
                                .items(new Schema<>()
                                        .type("object")
                                        .addProperty("field", new Schema<>().type("string").description("Field name"))
                                        .addProperty("message", new Schema<>().type("string").description("Validation error message"))))
                        .required(List.of("error", "message", "code", "timestamp", "validationErrors"))
        );
    }
} 