package com.kds.mock.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Schema(
    description = "Standard error response object",
    example = """
        {
            "error": "Validation failed",
            "message": "Request validation failed",
            "details": [
                "Path is required",
                "HTTP method is required"
            ],
            "timestamp": "2024-01-15T10:30:00",
            "status": 400
        }
        """
)
public class ErrorResponse {
    
    @Schema(description = "Error type or category", example = "Validation failed")
    private String error;
    
    @Schema(description = "Human-readable error message", example = "Request validation failed")
    private String message;
    
    @Schema(description = "List of detailed error messages", example = "[\"Path is required\", \"HTTP method is required\"]")
    private List<String> details;
    
    @Schema(description = "Timestamp when the error occurred", example = "2024-01-15T10:30:00")
    private LocalDateTime timestamp;
    
    @Schema(description = "HTTP status code", example = "400")
    private int status;

    public ErrorResponse(String error, String message) {
        this.error = error;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(String error, String message, List<String> details, int status) {
        this.error = error;
        this.message = message;
        this.details = details;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }
} 