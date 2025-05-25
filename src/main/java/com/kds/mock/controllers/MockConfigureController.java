package com.kds.mock.controllers;

import com.kds.mock.dto.MockEndpointRequest;
import com.kds.mock.dto.MockEndpointResponse;
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

@RestController
@RequestMapping("/endpoints")
@Tag(
    name = "Endpoint Configuration", 
    description = "APIs for configuring and managing mock endpoints. Use these endpoints to create, retrieve, and manage your mock API configurations."
)
@RequiredArgsConstructor
public class MockConfigureController {

    private MockEndpointConfigureService mockEndpointConfigureService;

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
            Returns an array of complete endpoint configurations including headers and response details.
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
                                    "description": "Mock users endpoint"
                                },
                                "headers": [
                                    {
                                        "id": 1,
                                        "name": "Content-Type",
                                        "value": "application/json"
                                    }
                                ],
                                "responses": {
                                    "id": 1,
                                    "method": "GET",
                                    "contentType": "application/json",
                                    "body": "{\\"users\\": []}"
                                }
                            },
                            {
                                "endpoints": {
                                    "id": 2,
                                    "path": "/api/products",
                                    "statusCode": 200,
                                    "description": "Mock products endpoint"
                                },
                                "headers": [
                                    {
                                        "id": 2,
                                        "name": "Content-Type",
                                        "value": "application/json"
                                    }
                                ],
                                "responses": {
                                    "id": 2,
                                    "method": "GET",
                                    "contentType": "application/json",
                                    "body": "{\\"products\\": []}"
                                }
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
                examples = @ExampleObject(
                    value = """
                        {
                            "error": "Internal server error",
                            "message": "Failed to retrieve endpoints"
                        }
                        """
                )
            )
        )
    })
    @GetMapping("/list")
    public ResponseEntity<List<MockEndpointResponse>> fetchMockedEndpoints() {
        List<MockEndpointResponse> endpoints = mockEndpointConfigureService.getAllMockEndpoints();
        return ResponseEntity.ok(endpoints);
    }

    @Operation(
            summary = "Configure a new mock endpoint",
            description = """
            Register a new mock endpoint configuration that will respond to requests at the specified path with the configured response.
            
            **Example Usage:**
            - Create a mock user API that returns user data
            - Set up error scenarios for testing
            - Mock third-party API responses for development
            
            **Important Notes:**
            - Each path can only have one configuration per HTTP method
            - Headers are optional and will be included in all responses
            - The response body can be any valid string content
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
                                "createdAt": "2024-01-15T10:30:00Z",
                                "updatedAt": "2024-01-15T10:30:00Z"
                            },
                            "headers": [
                                {
                                    "id": 1,
                                    "name": "Content-Type",
                                    "value": "application/json"
                                }
                            ],
                            "responses": {
                                "id": 1,
                                "method": "GET",
                                "contentType": "application/json",
                                "body": "{\\"users\\": [{\\"id\\": 1, \\"name\\": \\"John Doe\\"}]}"
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
                            examples = @ExampleObject(
                                    name = "Validation Error",
                                    value = """
                        {
                            "error": "Validation failed",
                            "details": [
                                "Path is required",
                                "HTTP method is required"
                            ]
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error - failed to save configuration",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Server Error",
                                    value = """
                        {
                            "error": "Internal server error",
                            "message": "Failed to save mock endpoint configuration"
                        }
                        """
                            )
                    )
            )
    })
    @PostMapping("/configure")
    public ResponseEntity<MockEndpointResponse> configureMockEndpoint(
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
                                        "Content-Type": "application/json"
                                    },
                                    "body": "{\\"users\\": [{\\"id\\": 1, \\"name\\": \\"John Doe\\"}]}",
                                    "contentType": "application/json",
                                    "description": "Mock users endpoint"
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
                                    "body": "{\\"error\\": \\"Resource not found\\"}",
                                    "contentType": "application/json",
                                    "description": "Mock error endpoint for testing"
                                }
                                """
                                    ),
                                    @ExampleObject(
                                            name = "POST Endpoint",
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
                                    "body": "{\\"id\\": 123, \\"name\\": \\"John Doe\\", \\"created\\": true}",
                                    "contentType": "application/json",
                                    "description": "Mock user creation endpoint"
                                }
                                """
                                    )
                            }
                    )
            )
            @Valid @RequestBody MockEndpointRequest request) {

        MockEndpointResponse response = mockEndpointConfigureService.saveMockEndpoint(request);

        if (response != null)
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        else
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

    }
}