package com.kds.mock.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.*;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@Schema(
    description = "Request object for configuring a new mock endpoint",
    example = """
        {
            "path": "/api/users",
            "method": "GET",
            "statusCode": 200,
            "responseHeaders": {
                "Content-Type": "application/json",
                "Cache-Control": "no-cache"
            },
            "body": "{\\"users\\": [{\\"id\\": 1, \\"name\\": \\"John Doe\\"}]}",
            "contentType": "application/json",
            "description": "Mock users endpoint for testing"
        }
        """
)
public class MockEndpointRequest {

    @Schema(
        description = "The URL path for the mock endpoint",
        example = "/api/users",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Path is required")
    @Pattern(regexp = "^/.*", message = "Path must start with /")
    @Size(max = 512, message = "Path cannot exceed 512 characters")
    private String path;

    @Schema(
        description = "HTTP method for the endpoint",
        example = "GET",
        allowableValues = {"GET", "POST", "PUT", "DELETE", "PATCH", "HEAD", "OPTIONS"},
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "HTTP method is required")
    @Pattern(regexp = "^(GET|POST|PUT|DELETE|PATCH|HEAD|OPTIONS)$", message = "Invalid HTTP method")
    private String method;

    @Schema(
        description = "HTTP status code to return",
        example = "200",
        minimum = "100",
        maximum = "599",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @Min(value = 100, message = "Status code must be between 100 and 599")
    @Max(value = 599, message = "Status code must be between 100 and 599")
    private int statusCode;

    @Schema(
        description = "Response headers to include in the mock response",
        example = "{\"Content-Type\": \"application/json\", \"Cache-Control\": \"no-cache\"}"
    )
    private Map<String, String> responseHeaders;

    @Schema(
        description = "Response body content",
        example = "{\"users\": [{\"id\": 1, \"name\": \"John Doe\"}]}"
    )
    @Size(max = 65535, message = "Response body cannot exceed 65535 characters")
    private String body;

    @Schema(
        description = "Content type of the response body",
        example = "application/json"
    )
    @Size(max = 255, message = "Content type cannot exceed 255 characters")
    private String contentType;

    @Schema(
        description = "Description of the mock endpoint",
        example = "Mock users endpoint for testing user management APIs"
    )
    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;
}
