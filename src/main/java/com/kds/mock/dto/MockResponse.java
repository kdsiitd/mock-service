package com.kds.mock.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpHeaders;

@Getter
@Setter
@NoArgsConstructor
@Schema(
    description = "Mock response object used internally for serving configured responses",
    hidden = true // This DTO is used internally and not directly exposed in API responses
)
public class MockResponse {
    
    @Schema(description = "HTTP status code of the response", example = "200")
    private int statusCode;
    
    @Schema(description = "HTTP headers to include in the response")
    private HttpHeaders headers;
    
    @Schema(description = "Response body content", example = "{\"message\": \"Hello World\"}")
    private String body;

    public MockResponse(int statusCode, HttpHeaders headers, String body) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.body = body;
    }
}
