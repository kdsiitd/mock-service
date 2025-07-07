package com.kds.mock.controllers;

import com.kds.mock.dto.MockResponse;
import com.kds.mock.service.MockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(
    name = "Mock Responses", 
    description = "Dynamic mock endpoints that serve configured responses. These endpoints are created automatically when you configure mock endpoints and will respond according to your configuration."
)
public class MockRequestController {

    private final MockService mockEndpointService;

    @Operation(
        summary = "Handle mock API requests",
        description = """
            This is a catch-all endpoint that handles requests to any path under `/api/**` and returns the configured mock response.
            
            **How it works:**
            1. Extracts the request path and HTTP method
            2. Looks up the corresponding mock configuration
            3. Returns the configured response with appropriate status code, headers, and body
            
            **Dynamic Behavior:**
            - The actual endpoint paths are determined by your mock configurations
            - Each configured endpoint will respond according to its specific configuration
            - Supports all HTTP methods (GET, POST, PUT, DELETE, etc.)
            
            **Examples:**
            - `GET /api/users` - Returns configured user data
            - `POST /api/users` - Returns configured creation response
            - `GET /api/products/123` - Returns configured product data
            
            **Note:** This endpoint documentation serves as a template. The actual available endpoints depend on your mock configurations.
            """,
        tags = {"Mock Responses"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successful mock response (or any other configured status code)",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "User List Response",
                        description = "Example response for GET /api/users",
                        value = """
                            {
                                "users": [
                                    {
                                        "id": 1,
                                        "name": "John Doe",
                                        "email": "john@example.com",
                                        "role": "admin",
                                        "active": true,
                                        "createdAt": "2024-01-15T10:30:00Z"
                                    },
                                    {
                                        "id": 2,
                                        "name": "Jane Smith", 
                                        "email": "jane@example.com",
                                        "role": "user",
                                        "active": true,
                                        "createdAt": "2024-01-14T15:20:00Z"
                                    }
                                ],
                                "total": 2,
                                "page": 1,
                                "limit": 10
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "User Creation Response",
                        description = "Example response for POST /api/users",
                        value = """
                            {
                                "id": 123,
                                "name": "New User",
                                "email": "newuser@example.com",
                                "role": "user",
                                "active": true,
                                "created": true,
                                "createdAt": "2024-01-15T10:30:00Z",
                                "message": "User created successfully"
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "Product Response",
                        description = "Example response for GET /api/products/123",
                        value = """
                            {
                                "id": 123,
                                "name": "Sample Product",
                                "description": "A high-quality sample product for testing",
                                "price": 29.99,
                                "currency": "USD",
                                "category": "Electronics",
                                "brand": "SampleBrand",
                                "inStock": true,
                                "stockQuantity": 150,
                                "tags": ["electronics", "gadget", "sample"],
                                "createdAt": "2024-01-10T08:00:00Z",
                                "updatedAt": "2024-01-15T10:30:00Z"
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "Order Response",
                        description = "Example response for GET /api/orders/456",
                        value = """
                            {
                                "id": 456,
                                "orderNumber": "ORD-2024-001",
                                "customerId": 123,
                                "status": "confirmed",
                                "items": [
                                    {
                                        "productId": 123,
                                        "productName": "Sample Product",
                                        "quantity": 2,
                                        "price": 29.99,
                                        "total": 59.98
                                    }
                                ],
                                "subtotal": 59.98,
                                "tax": 5.40,
                                "shipping": 9.99,
                                "total": 75.37,
                                "currency": "USD",
                                "shippingAddress": {
                                    "street": "123 Main St",
                                    "city": "Anytown",
                                    "state": "CA",
                                    "zipCode": "12345",
                                    "country": "USA"
                                },
                                "createdAt": "2024-01-15T10:30:00Z",
                                "estimatedDelivery": "2024-01-20T00:00:00Z"
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "XML Response",
                        description = "Example XML response for legacy systems",
                        value = """
                            <?xml version="1.0" encoding="UTF-8"?>
                            <response>
                                <status>success</status>
                                <data>
                                    <users>
                                        <user id="1">
                                            <name>John Doe</name>
                                            <email>john@example.com</email>
                                            <active>true</active>
                                        </user>
                                        <user id="2">
                                            <name>Jane Smith</name>
                                            <email>jane@example.com</email>
                                            <active>true</active>
                                        </user>
                                    </users>
                                </data>
                            </response>
                            """
                    ),
                    @ExampleObject(
                        name = "CSV Response",
                        description = "Example CSV response for data export",
                        value = """
                            id,name,email,role,active,createdAt
                            1,John Doe,john@example.com,admin,true,2024-01-15T10:30:00Z
                            2,Jane Smith,jane@example.com,user,true,2024-01-14T15:20:00Z
                            3,Bob Johnson,bob@example.com,user,false,2024-01-13T12:15:00Z
                            """
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "201",
            description = "Created - typically for POST requests",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                        {
                            "id": 123,
                            "created": true,
                            "message": "Resource created successfully"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad Request - for configured error scenarios",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                        {
                            "error": "Bad Request",
                            "message": "Invalid input data",
                            "code": 400
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Mock endpoint not configured for this path and method",
            content = @Content(
                mediaType = "text/plain",
                examples = @ExampleObject(
                    name = "Endpoint Not Configured",
                    value = "Mock endpoint not configured."
                )
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal Server Error - for configured error scenarios",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                        {
                            "error": "Internal Server Error",
                            "message": "Something went wrong",
                            "code": 500
                        }
                        """
                )
            )
        )
    })
    @RequestMapping(value = "/**")
    public ResponseEntity<?> handleMockRequest(
            @Parameter(
                description = """
                    HTTP request containing:
                    - **Path**: The API endpoint path (e.g., /api/users, /api/products/123)
                    - **Method**: HTTP method (GET, POST, PUT, DELETE, etc.)
                    - **Headers**: Optional request headers
                    - **Body**: Optional request body (for POST/PUT requests)
                    
                    The path and method are used to look up the corresponding mock configuration.
                    """,
                hidden = true // Hide from Swagger as it's automatically handled
            )
            HttpServletRequest request) {
        MockResponse response = mockEndpointService.getMockResponseByPathAndMethod(request.getRequestURI(), request.getMethod());
        if (response == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Mock endpoint not configured.");
        }
        return ResponseEntity
                .status(response.getStatusCode())
                .headers(response.getHeaders())
                .body(response.getBody());
    }
}
