package com.kds.mock.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@Schema(
    description = "Request object for updating an existing mock endpoint",
    example = """
        {
            "statusCode": 200,
            "responseHeaders": {
                "Content-Type": "application/json",
                "Cache-Control": "max-age=3600"
            },
            "body": "{\\"users\\": [{\\"id\\": 1, \\"name\\": \\"Updated User\\"}]}",
            "contentType": "application/json",
            "description": "Updated mock users endpoint"
        }
        """
)
public class UpdateMockEndpointRequest {

    @Schema(
        description = "HTTP status code to return",
        example = "200",
        minimum = "100",
        maximum = "599"
    )
    @Min(value = 100, message = "Status code must be between 100 and 599")
    @Max(value = 599, message = "Status code must be between 100 and 599")
    private Integer statusCode;

    @Schema(
        description = "Response headers to include in the mock response",
        example = "{\"Content-Type\": \"application/json\", \"Cache-Control\": \"max-age=3600\"}"
    )
    @Size(max = 50, message = "Cannot have more than 50 response headers")
    private Map<@NotBlank(message = "Header name cannot be blank") 
                @Size(max = 100, message = "Header name cannot exceed 100 characters") String, 
                @NotNull(message = "Header value cannot be null")
                @Size(max = 1000, message = "Header value cannot exceed 1000 characters") String> responseHeaders;

    @Schema(
        description = "Response body content",
        example = "{\"users\": [{\"id\": 1, \"name\": \"Updated User\"}]}"
    )
    @Size(max = 65535, message = "Response body cannot exceed 65535 characters")
    private String body;

    @Schema(
        description = "Content type of the response body",
        example = "application/json"
    )
    @Size(max = 255, message = "Content type cannot exceed 255 characters")
    @Pattern(regexp = "^[a-zA-Z0-9][a-zA-Z0-9!#$&\\-\\^_]*\\/[a-zA-Z0-9][a-zA-Z0-9!#$&\\-\\^_]*$|^$", 
             message = "Invalid content type format")
    private String contentType;

    @Schema(
        description = "Description of the mock endpoint",
        example = "Updated mock users endpoint"
    )
    @Size(min = 5, max = 255, message = "Description must be between 5 and 255 characters")
    private String description;

    @Schema(
        description = "Load testing configuration for simulating realistic system behavior",
        example = """
            {
                "latencyConfig": {
                    "type": "PERCENTILE",
                    "p50": 100,
                    "p95": 500,
                    "p99": 1000
                },
                "timeoutConfig": {
                    "enabled": true,
                    "timeoutMs": 5000,
                    "timeoutProbability": 0.01
                },
                "errorConfig": {
                    "enabled": true,
                    "errorRate": 0.05,
                    "errorStatusCodes": [500, 502, 503]
                }
            }
            """
    )
    @Valid
    private LoadTestConfig loadTestConfig;

    /**
     * Custom validation method to ensure content type matches the body format
     */
    @AssertTrue(message = "Content type should be specified when body is provided")
    private boolean isContentTypeValidWithBody() {
        if (body != null && !body.trim().isEmpty()) {
            return contentType != null && !contentType.trim().isEmpty();
        }
        return true;
    }
} 