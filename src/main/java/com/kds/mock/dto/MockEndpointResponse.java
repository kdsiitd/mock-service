package com.kds.mock.dto;

import com.kds.mock.entity.Endpoints;
import com.kds.mock.entity.Headers;
import com.kds.mock.entity.Responses;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Schema(
    description = "Complete mock endpoint configuration response",
    example = """
        {
            "endpoints": {
                "id": 1,
                "path": "/api/users",
                "statusCode": 200,
                "description": "Mock users endpoint",
                "loadTestConfig": {
                  "latencyConfig": {"type": "FIXED", "fixedLatencyMs": 100}
                },
                "createdAt": "2024-01-15T10:30:00Z",
                "updatedAt": "2024-01-15T10:30:00Z"
            },
            "headers": [
                {
                    "id": 1,
                    "name": "Content-Type",
                    "value": "application/json",
                    "createdAt": "2024-01-15T10:30:00Z",
                    "updatedAt": "2024-01-15T10:30:00Z"
                },
                {
                    "id": 2,
                    "name": "Cache-Control",
                    "value": "no-cache",
                    "createdAt": "2024-01-15T10:30:00Z",
                    "updatedAt": "2024-01-15T10:30:00Z"
                }
            ],
            "responses": {
                "id": 1,
                "method": "GET",
                "contentType": "application/json",
                "body": "{\"users\": [{\"id\": 1, \"name\": \"John Doe\"}]}",
                "createdAt": "2024-01-15T10:30:00Z",
                "updatedAt": "2024-01-15T10:30:00Z"
            }
        }
        """
)
public class MockEndpointResponse {
    
    @Schema(
        description = "Endpoint configuration details including path, status code, and metadata",
        implementation = Endpoints.class
    )
    private Endpoints endpoints;
    
    @Schema(
        description = "List of HTTP headers to include in the response",
        implementation = Headers.class
    )
    private List<Headers> headers;
    
    @Schema(
        description = "Response configuration including method, content type, and body",
        implementation = Responses.class
    )
    private Responses responses;

    @Schema(
        description = "Deserialized load testing configuration for this endpoint, if present"
    )
    private LoadTestConfig loadTestConfig;

    public MockEndpointResponse(Endpoints endpoints, List<Headers> headers, Responses responses) {
        this.endpoints = endpoints;
        this.headers = headers;
        this.responses = responses;
    }

    public MockEndpointResponse(Endpoints endpoints, List<Headers> headers, Responses responses, LoadTestConfig loadTestConfig) {
        this.endpoints = endpoints;
        this.headers = headers;
        this.responses = responses;
        this.loadTestConfig = loadTestConfig;
    }
}
