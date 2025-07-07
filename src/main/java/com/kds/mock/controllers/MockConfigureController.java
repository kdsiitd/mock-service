package com.kds.mock.controllers;

import com.kds.mock.dto.MockEndpointRequest;
import com.kds.mock.dto.MockEndpointResponse;
import com.kds.mock.dto.UpdateMockEndpointRequest;
import com.kds.mock.service.MockEndpointConfigureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/endpoints")
@Tag(
    name = "Endpoint Configuration", 
    description = "APIs for configuring and managing mock endpoints. Create, read, update, and delete mock API configurations with advanced features like load testing simulation."
)
@RequiredArgsConstructor
public class MockConfigureController {

    private final MockEndpointConfigureService mockEndpointConfigureService;

    @Operation(
        summary = "List all configured mock endpoints",
        description = """
            Retrieves a list of all currently configured mock endpoints with their complete configuration details.
            
            **Use Cases:**
            - View all configured mock endpoints
            - Audit existing configurations
            - Debug endpoint configurations
            - Export configurations for backup
            
            **Response Format:**
            Returns an array of complete endpoint configurations including headers, response details, and load testing configurations.
            """,
        tags = {"Endpoint Configuration"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved all configured endpoints",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    type = "array",
                    implementation = MockEndpointResponse.class
                ),
                examples = @ExampleObject(
                    name = "Multiple Endpoints",
                    value = """
                        [
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
                                    "body": "{\\"users\\": [{\\"id\\": 1, \\"name\\": \\"John Doe\\"}]}",
                                    "createdAt": "2024-01-15T10:30:00.000",
                                    "updatedAt": "2024-01-15T10:30:00.000"
                                },
                                "loadTestConfig": {
                                    "latencyConfig": {
                                        "type": "FIXED",
                                        "fixedLatencyMs": 100
                                    },
                                    "errorConfig": {
                                        "enabled": false
                                    }
                                }
                            },
                            {
                                "endpoints": {
                                    "id": 2,
                                    "path": "/api/products",
                                    "statusCode": 200,
                                    "description": "Mock products endpoint",
                                    "createdAt": "2024-01-15T10:30:00.000",
                                    "updatedAt": "2024-01-15T10:30:00.000"
                                },
                                "headers": [
                                    {
                                        "id": 2,
                                        "name": "Content-Type",
                                        "value": "application/json",
                                        "createdAt": "2024-01-15T10:30:00.000",
                                        "updatedAt": "2024-01-15T10:30:00.000"
                                    }
                                ],
                                "responses": {
                                    "id": 2,
                                    "method": "GET",
                                    "contentType": "application/json",
                                    "body": "{\\"products\\": []}",
                                    "createdAt": "2024-01-15T10:30:00.000",
                                    "updatedAt": "2024-01-15T10:30:00.000"
                                },
                                "loadTestConfig": null
                            }
                        ]
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Map.class),
                examples = @ExampleObject(
                    name = "Server Error",
                    value = """
                        {
                            "error": "Internal Server Error",
                            "message": "Failed to retrieve endpoints",
                            "status": 500,
                            "timestamp": "2024-01-15T10:30:00.000",
                            "path": "/endpoints"
                        }
                        """
                )
            )
        )
    })
    @GetMapping
    public ResponseEntity<List<MockEndpointResponse>> getAllMockEndpoints() {
        List<MockEndpointResponse> endpoints = mockEndpointConfigureService.getAllMockEndpoints();
        return ResponseEntity.ok(endpoints);
    }

    @Operation(
            summary = "Configure a new mock endpoint",
            description = """
            Register a new mock endpoint configuration that will respond to requests at the specified path with the configured response.
            
            **Key Features:**
            - Support for all HTTP methods (GET, POST, PUT, DELETE, etc.)
            - Custom status codes, headers, and response bodies
            - Advanced load testing simulation with latency, errors, and rate limiting
            - Persistent storage with automatic validation
            
            **Important Notes:**
            - Each path can only have one configuration per HTTP method
            - Headers are optional and will be included in all responses
            - The response body can be any valid string content (JSON, XML, plain text, etc.)
            - Load testing configuration is optional but provides realistic simulation
            """,
            tags = {"Endpoint Configuration"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Mock endpoint successfully configured",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MockEndpointResponse.class),
                            examples = @ExampleObject(
                                    name = "Successful Configuration",
                                    value = """
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
                                "body": "{\\"users\\": [{\\"id\\": 1, \\"name\\": \\"John Doe\\"}]}",
                                "createdAt": "2024-01-15T10:30:00.000",
                                "updatedAt": "2024-01-15T10:30:00.000"
                            },
                            "loadTestConfig": {
                                "latencyConfig": {
                                    "type": "FIXED",
                                    "fixedLatencyMs": 100
                                },
                                "errorConfig": {
                                    "enabled": false
                                }
                            }
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data - validation errors",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Validation Error",
                                            description = "Request validation failed",
                                            value = """
                                {
                                    "error": "Bad Request",
                                    "message": "Validation failed",
                                    "status": 400,
                                    "timestamp": "2024-01-15T10:30:00.000",
                                    "path": "/endpoints",
                                    "validationErrors": [
                                        {
                                            "field": "path",
                                            "rejectedValue": "",
                                            "message": "Path is required"
                                        },
                                        {
                                            "field": "method",
                                            "rejectedValue": "INVALID",
                                            "message": "Invalid HTTP method. Allowed values: GET, POST, PUT, DELETE, PATCH, HEAD, OPTIONS"
                                        },
                                        {
                                            "field": "statusCode",
                                            "rejectedValue": 999,
                                            "message": "Status code must be between 100 and 599"
                                        }
                                    ]
                                }
                                """
                                    ),
                                    @ExampleObject(
                                            name = "Duplicate Endpoint",
                                            description = "Endpoint already exists",
                                            value = """
                                {
                                    "error": "Conflict",
                                    "message": "Mock endpoint already exists for path: /api/users and method: GET",
                                    "status": 409,
                                    "timestamp": "2024-01-15T10:30:00.000",
                                    "path": "/endpoints"
                                }
                                """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error - failed to save configuration",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(
                                    name = "Server Error",
                                    value = """
                        {
                            "error": "Internal Server Error",
                            "message": "Failed to save mock endpoint configuration",
                            "status": 500,
                            "timestamp": "2024-01-15T10:30:00.000",
                            "path": "/endpoints"
                        }
                        """
                            )
                    )
            )
    })
    @PostMapping
    public ResponseEntity<MockEndpointResponse> createMockEndpoint(
            @Parameter(
                    description = "Mock endpoint configuration details",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = MockEndpointRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Simple GET Endpoint",
                                            description = "Basic GET endpoint returning JSON data",
                                            value = """
                                {
                                    "path": "/api/users",
                                    "method": "GET",
                                    "statusCode": 200,
                                    "responseHeaders": {
                                        "Content-Type": "application/json",
                                        "Cache-Control": "no-cache"
                                    },
                                    "body": "{\\"users\\": [{\\"id\\": 1, \\"name\\": \\"John Doe\\", \\"email\\": \\"john@example.com\\"}]}",
                                    "contentType": "application/json",
                                    "description": "Mock users endpoint"
                                }
                                """
                                    ),
                                    @ExampleObject(
                                            name = "POST Endpoint with Creation Response",
                                            description = "POST endpoint for creating resources",
                                            value = """
                                {
                                    "path": "/api/users",
                                    "method": "POST",
                                    "statusCode": 201,
                                    "responseHeaders": {
                                        "Content-Type": "application/json",
                                        "Location": "/api/users/123"
                                    },
                                    "body": "{\\"id\\": 123, \\"name\\": \\"John Doe\\", \\"email\\": \\"john@example.com\\", \\"created\\": true, \\"createdAt\\": \\"2024-01-15T10:30:00Z\\"}",
                                    "contentType": "application/json",
                                    "description": "Mock user creation endpoint"
                                }
                                """
                                    ),
                                    @ExampleObject(
                                            name = "Error Response Endpoint",
                                            description = "Endpoint that returns an error response",
                                            value = """
                                {
                                    "path": "/api/error",
                                    "method": "GET",
                                    "statusCode": 404,
                                    "responseHeaders": {
                                        "Content-Type": "application/json"
                                    },
                                    "body": "{\\"error\\": \\"Not Found\\", \\"message\\": \\"Resource not found\\", \\"status\\": 404}",
                                    "contentType": "application/json",
                                    "description": "Mock error endpoint for testing"
                                }
                                """
                                    ),
                                    @ExampleObject(
                                            name = "Load Testing Endpoint",
                                            description = "Endpoint with comprehensive load testing configuration",
                                            value = """
                                {
                                    "path": "/api/load-test",
                                    "method": "GET",
                                    "statusCode": 200,
                                    "responseHeaders": {
                                        "Content-Type": "application/json",
                                        "X-Response-Time": "150ms"
                                    },
                                    "body": "{\\"message\\": \\"Load test response\\", \\"timestamp\\": \\"2024-01-15T10:30:00Z\\", \\"server\\": \\"mock-01\\"}",
                                    "contentType": "application/json",
                                    "description": "Load testing endpoint with realistic latency and error simulation",
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
                                }
                                """
                                    ),
                                    @ExampleObject(
                                            name = "XML Response Endpoint",
                                            description = "Endpoint returning XML content for legacy systems",
                                            value = """
                                {
                                    "path": "/api/xml-data",
                                    "method": "GET",
                                    "statusCode": 200,
                                    "responseHeaders": {
                                        "Content-Type": "application/xml",
                                        "X-Content-Version": "1.0"
                                    },
                                    "body": "<?xml version=\\"1.0\\" encoding=\\"UTF-8\\"?><users><user id=\\"1\\"><name>John Doe</name><email>john@example.com</email></user><user id=\\"2\\"><name>Jane Smith</name><email>jane@example.com</email></user></users>",
                                    "contentType": "application/xml",
                                    "description": "Mock XML endpoint for legacy systems"
                                }
                                """
                                    ),
                                    @ExampleObject(
                                            name = "File Upload Response",
                                            description = "Endpoint simulating file upload success",
                                            value = """
                                {
                                    "path": "/api/upload",
                                    "method": "POST",
                                    "statusCode": 201,
                                    "responseHeaders": {
                                        "Content-Type": "application/json",
                                        "Location": "/api/files/abc123"
                                    },
                                    "body": "{\\"fileId\\": \\"abc123\\", \\"filename\\": \\"document.pdf\\", \\"size\\": 1024000, \\"uploadedAt\\": \\"2024-01-15T10:30:00Z\\", \\"status\\": \\"uploaded\\"}",
                                    "contentType": "application/json",
                                    "description": "Mock file upload endpoint"
                                }
                                """
                                    )
                            }
                    )
            )
            @Valid @RequestBody MockEndpointRequest request) {

        try {
            MockEndpointResponse response = mockEndpointConfigureService.saveMockEndpoint(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            throw e; // Let global exception handler handle it
        } catch (Exception e) {
            throw new RuntimeException("Failed to create mock endpoint", e);
        }
    }

    @Operation(
        summary = "Update an existing mock endpoint",
        description = """
            Updates an existing mock endpoint configuration by path and method.
            
            **Use Cases:**
            - Modify response data for existing endpoints
            - Update status codes or headers
            - Change load testing configuration
            - Update endpoint descriptions
            
            **Important Notes:**
            - The endpoint must exist to be updated
            - All fields are optional - only provided fields will be updated
            - Load testing configuration can be updated independently
            - Path must be URL encoded when passed in URL
            """,
        tags = {"Endpoint Configuration"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Mock endpoint successfully updated",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = MockEndpointResponse.class),
                examples = @ExampleObject(
                    name = "Updated Configuration",
                    value = """
                        {
                            "endpoints": {
                                "id": 1,
                                "path": "/api/users",
                                "statusCode": 200,
                                "description": "Updated mock users endpoint",
                                "createdAt": "2024-01-15T10:30:00.000",
                                "updatedAt": "2024-01-15T11:30:00.000"
                            },
                            "headers": [
                                {
                                    "id": 1,
                                    "name": "Content-Type",
                                    "value": "application/json",
                                    "createdAt": "2024-01-15T10:30:00.000",
                                    "updatedAt": "2024-01-15T11:30:00.000"
                                },
                                {
                                    "id": 2,
                                    "name": "Cache-Control",
                                    "value": "max-age=3600",
                                    "createdAt": "2024-01-15T11:30:00.000",
                                    "updatedAt": "2024-01-15T11:30:00.000"
                                }
                            ],
                            "responses": {
                                "id": 1,
                                "method": "GET",
                                "contentType": "application/json",
                                "body": "{\\"users\\": [{\\"id\\": 1, \\"name\\": \\"Updated User\\"}]}",
                                "createdAt": "2024-01-15T10:30:00.000",
                                "updatedAt": "2024-01-15T11:30:00.000"
                            },
                            "loadTestConfig": {
                                "latencyConfig": {
                                    "type": "FIXED",
                                    "fixedLatencyMs": 250
                                },
                                "errorConfig": {
                                    "enabled": true,
                                    "errorRate": 0.1,
                                    "errorStatusCodes": [503]
                                }
                            }
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request data",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Map.class),
                examples = @ExampleObject(
                    name = "Validation Error",
                    value = """
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
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Mock endpoint not found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Map.class),
                examples = @ExampleObject(
                    name = "Not Found",
                    value = """
                        {
                            "error": "Not Found",
                            "message": "Mock endpoint not found for path: /api/users and method: GET",
                            "status": 404,
                            "timestamp": "2024-01-15T10:30:00.000",
                            "path": "/endpoints/api%2Fusers"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Map.class),
                examples = @ExampleObject(
                    name = "Server Error",
                    value = """
                        {
                            "error": "Internal Server Error",
                            "message": "Failed to update mock endpoint",
                            "status": 500,
                            "timestamp": "2024-01-15T10:30:00.000",
                            "path": "/endpoints/api%2Fusers"
                        }
                        """
                )
            )
        )
    })
    @PutMapping("/{path:.*}")
    public ResponseEntity<MockEndpointResponse> updateMockEndpoint(
            @Parameter(
                description = "URL path of the endpoint to update (must be URL encoded)",
                example = "/api/users",
                required = true
            )
            @PathVariable String path,
            @Parameter(
                description = "HTTP method of the endpoint to update",
                example = "GET",
                required = true
            )
            @RequestParam String method,
            @Parameter(
                description = "Updated mock endpoint configuration. All fields are optional - only provided fields will be updated.",
                required = true,
                content = @Content(
                    schema = @Schema(implementation = UpdateMockEndpointRequest.class),
                    examples = {
                        @ExampleObject(
                            name = "Update Response Body and Status",
                            description = "Update response body and status code",
                            value = """
                                {
                                    "statusCode": 200,
                                    "body": "{\\"users\\": [{\\"id\\": 1, \\"name\\": \\"Updated User\\", \\"email\\": \\"updated@example.com\\"}]}",
                                    "description": "Updated mock users endpoint"
                                }
                                """
                        ),
                        @ExampleObject(
                            name = "Update Headers Only",
                            description = "Update only response headers",
                            value = """
                                {
                                    "responseHeaders": {
                                        "Content-Type": "application/json",
                                        "Cache-Control": "max-age=3600",
                                        "X-Custom-Header": "custom-value",
                                        "Access-Control-Allow-Origin": "*"
                                    }
                                }
                                """
                        ),
                        @ExampleObject(
                            name = "Update Status Code Only",
                            description = "Update only the status code",
                            value = """
                                {
                                    "statusCode": 404
                                }
                                """
                        ),
                        @ExampleObject(
                            name = "Update with Load Testing",
                            description = "Update endpoint with load testing configuration",
                            value = """
                                {
                                    "statusCode": 200,
                                    "body": "{\\"message\\": \\"Load test updated\\", \\"timestamp\\": \\"2024-01-15T10:30:00Z\\"}",
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
                                        },
                                        "rateLimitConfig": {
                                            "enabled": true,
                                            "requestsPerSecond": 50,
                                            "burstSize": 5,
                                            "rateLimitStatusCode": 429
                                        }
                                    }
                                }
                                """
                        ),
                        @ExampleObject(
                            name = "Update Content Type and Body",
                            description = "Change response format to XML",
                            value = """
                                {
                                    "contentType": "application/xml",
                                    "body": "<?xml version=\\"1.0\\" encoding=\\"UTF-8\\"?><users><user id=\\"1\\"><name>Updated User</name></user></users>",
                                    "responseHeaders": {
                                        "Content-Type": "application/xml"
                                    }
                                }
                                """
                        )
                    }
                )
            )
            @Valid @RequestBody UpdateMockEndpointRequest request) {
        
        try {
            // Decode the path parameter
            String decodedPath = java.net.URLDecoder.decode(path, "UTF-8");
            MockEndpointResponse response = mockEndpointConfigureService.updateMockEndpoint(decodedPath, method, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to update mock endpoint", e);
        }
    }

    @Operation(
        summary = "Delete a mock endpoint",
        description = """
            Removes a mock endpoint configuration by path and method.
            
            **Use Cases:**
            - Remove outdated mock endpoints
            - Clean up test configurations
            - Remove temporary endpoints
            - Cleanup after testing
            
            **Important Notes:**
            - This operation is irreversible
            - All associated headers and responses will be deleted
            - The endpoint must exist to be deleted
            - Path must be URL encoded when passed in URL
            """,
        tags = {"Endpoint Configuration"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Mock endpoint successfully deleted",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Map.class),
                examples = @ExampleObject(
                    name = "Delete Success",
                    value = """
                        {
                            "message": "Mock endpoint deleted successfully",
                            "deletedPath": "/api/users",
                            "deletedMethod": "GET",
                            "timestamp": "2024-01-15T10:30:00.000"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request parameters",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Map.class),
                examples = @ExampleObject(
                    name = "Invalid Parameters",
                    value = """
                        {
                            "error": "Bad Request",
                            "message": "Invalid path or method parameter",
                            "status": 400,
                            "timestamp": "2024-01-15T10:30:00.000",
                            "path": "/endpoints/invalid-path"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Mock endpoint not found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Map.class),
                examples = @ExampleObject(
                    name = "Not Found",
                    value = """
                        {
                            "error": "Not Found",
                            "message": "Mock endpoint not found for path: /api/users and method: GET",
                            "status": 404,
                            "timestamp": "2024-01-15T10:30:00.000",
                            "path": "/endpoints/api%2Fusers"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Map.class),
                examples = @ExampleObject(
                    name = "Server Error",
                    value = """
                        {
                            "error": "Internal Server Error",
                            "message": "Failed to delete mock endpoint",
                            "status": 500,
                            "timestamp": "2024-01-15T10:30:00.000",
                            "path": "/endpoints/api%2Fusers"
                        }
                        """
                )
            )
        )
    })
    @DeleteMapping("/{path:.*}")
    public ResponseEntity<Map<String, String>> deleteMockEndpoint(
            @Parameter(
                description = "URL path of the endpoint to delete (must be URL encoded)",
                example = "/api/users",
                required = true
            )
            @PathVariable String path,
            @Parameter(
                description = "HTTP method of the endpoint to delete",
                example = "GET",
                required = true
            )
            @RequestParam String method) {
        
        try {
            // Decode the path parameter
            String decodedPath = java.net.URLDecoder.decode(path, "UTF-8");
            mockEndpointConfigureService.deleteMockEndpoint(decodedPath, method);
            
            Map<String, String> response = new java.util.HashMap<>();
            response.put("message", "Mock endpoint deleted successfully");
            response.put("deletedPath", decodedPath);
            response.put("deletedMethod", method);
            response.put("timestamp", java.time.LocalDateTime.now().toString());
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete mock endpoint", e);
        }
    }

    @Operation(
        summary = "Get a specific mock endpoint by ID",
        description = """
            Retrieves a specific mock endpoint configuration by its unique ID.
            
            **Use Cases:**
            - View specific endpoint configuration when you know the ID
            - Debug endpoint issues using the ID from list responses
            - Export specific configurations by ID
            - Direct access to endpoint details without path encoding
            
            **Response Format:**
            Returns the complete endpoint configuration including headers, response details, and load testing configuration.
            """,
        tags = {"Endpoint Configuration"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved the mock endpoint",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = MockEndpointResponse.class),
                examples = @ExampleObject(
                    name = "Endpoint Details by ID",
                    value = """
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
                                },
                                {
                                    "id": 2,
                                    "name": "Cache-Control",
                                    "value": "no-cache",
                                    "createdAt": "2024-01-15T10:30:00.000",
                                    "updatedAt": "2024-01-15T10:30:00.000"
                                }
                            ],
                            "responses": {
                                "id": 1,
                                "method": "GET",
                                "contentType": "application/json",
                                "body": "{\\"users\\": [{\\"id\\": 1, \\"name\\": \\"John Doe\\"}]}",
                                "createdAt": "2024-01-15T10:30:00.000",
                                "updatedAt": "2024-01-15T10:30:00.000"
                            },
                            "loadTestConfig": {
                                "latencyConfig": {
                                    "type": "FIXED",
                                    "fixedLatencyMs": 100
                                },
                                "errorConfig": {
                                    "enabled": false
                                }
                            }
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid ID format",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Map.class),
                examples = @ExampleObject(
                    name = "Invalid ID",
                    value = """
                        {
                            "error": "Bad Request",
                            "message": "Invalid endpoint ID format",
                            "status": 400,
                            "timestamp": "2024-01-15T10:30:00.000",
                            "path": "/endpoints/id/invalid"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Mock endpoint not found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Map.class),
                examples = @ExampleObject(
                    name = "Not Found",
                    value = """
                        {
                            "error": "Not Found",
                            "message": "Mock endpoint not found with ID: 123",
                            "status": 404,
                            "timestamp": "2024-01-15T10:30:00.000",
                            "path": "/endpoints/id/123"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Map.class),
                examples = @ExampleObject(
                    name = "Server Error",
                    value = """
                        {
                            "error": "Internal Server Error",
                            "message": "Failed to retrieve mock endpoint",
                            "status": 500,
                            "timestamp": "2024-01-15T10:30:00.000",
                            "path": "/endpoints/id/123"
                        }
                        """
                )
            )
        )
    })
    @GetMapping("/id/{id}")
    public ResponseEntity<MockEndpointResponse> getEndpointById(
            @Parameter(
                description = "Unique identifier of the endpoint to retrieve",
                example = "1",
                required = true
            )
            @PathVariable Long id) {
        
        try {
            MockEndpointResponse response = mockEndpointConfigureService.getMockEndpointById(id);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve mock endpoint by ID", e);
        }
    }

    @Operation(
        summary = "Get a specific mock endpoint by path and method",
        description = """
            Retrieves a specific mock endpoint configuration by path and method.
            
            **Use Cases:**
            - View specific endpoint configuration
            - Debug endpoint issues
            - Export specific configurations
            - Verify endpoint configuration before testing
            
            **Response Format:**
            Returns the complete endpoint configuration including headers, response details, and load testing configuration.
            """,
        tags = {"Endpoint Configuration"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved the mock endpoint",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = MockEndpointResponse.class),
                examples = @ExampleObject(
                    name = "Endpoint Details",
                    value = """
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
                                "body": "{\\"users\\": [{\\"id\\": 1, \\"name\\": \\"John Doe\\"}]}",
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
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request parameters",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Map.class),
                examples = @ExampleObject(
                    name = "Invalid Parameters",
                    value = """
                        {
                            "error": "Bad Request",
                            "message": "Invalid path or method parameter",
                            "status": 400,
                            "timestamp": "2024-01-15T10:30:00.000",
                            "path": "/endpoints/invalid-path"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Mock endpoint not found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Map.class),
                examples = @ExampleObject(
                    name = "Not Found",
                    value = """
                        {
                            "error": "Not Found",
                            "message": "Mock endpoint not found for path: /api/users and method: GET",
                            "status": 404,
                            "timestamp": "2024-01-15T10:30:00.000",
                            "path": "/endpoints/api%2Fusers"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Map.class),
                examples = @ExampleObject(
                    name = "Server Error",
                    value = """
                        {
                            "error": "Internal Server Error",
                            "message": "Failed to retrieve mock endpoint",
                            "status": 500,
                            "timestamp": "2024-01-15T10:30:00.000",
                            "path": "/endpoints/api%2Fusers"
                        }
                        """
                )
            )
        )
    })
    @GetMapping("/{path:.*}")
    public ResponseEntity<MockEndpointResponse> getMockEndpoint(
            @Parameter(
                description = "URL path of the endpoint to retrieve (must be URL encoded)",
                example = "/api/users",
                required = true
            )
            @PathVariable String path,
            @Parameter(
                description = "HTTP method of the endpoint to retrieve",
                example = "GET",
                required = true
            )
            @RequestParam String method) {
        
        try {
            // Decode the path parameter
            String decodedPath = java.net.URLDecoder.decode(path, "UTF-8");
            MockEndpointResponse response = mockEndpointConfigureService.getMockEndpoint(decodedPath, method);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve mock endpoint", e);
        }
    }
}