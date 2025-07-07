package com.kds.mock.dto;

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
    description = "Load testing configuration for mock endpoints",
    example = """
        {
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
        """
)
public class LoadTestConfig {
    
    @Schema(
        description = "Latency configuration for simulating realistic response times",
        example = """
            {
                "type": "PERCENTILE",
                "p50": 100,
                "p95": 500,
                "p99": 1000,
                "p999": 2000
            }
            """
    )
    private LatencyConfig latencyConfig;
    
    @Schema(
        description = "Timeout configuration for simulating request timeouts",
        example = """
            {
                "enabled": true,
                "timeoutMs": 5000,
                "timeoutProbability": 0.01
            }
            """
    )
    private TimeoutConfig timeoutConfig;
    
    @Schema(
        description = "Error configuration for simulating service failures",
        example = """
            {
                "enabled": true,
                "errorRate": 0.05,
                "errorStatusCodes": [500, 502, 503],
                "errorMessages": {
                    "500": "Internal server error",
                    "502": "Bad gateway",
                    "503": "Service unavailable"
                }
            }
            """
    )
    private ErrorConfig errorConfig;
    
    @Schema(
        description = "Rate limiting configuration for controlling request throughput",
        example = """
            {
                "enabled": true,
                "requestsPerSecond": 100,
                "burstSize": 10,
                "rateLimitStatusCode": 429
            }
            """
    )
    private RateLimitConfig rateLimitConfig;

    @Getter
    @Setter
    @NoArgsConstructor
    @Schema(description = "Latency configuration settings")
    public static class LatencyConfig {
        
        @Schema(
            description = "Type of latency simulation",
            example = "PERCENTILE",
            allowableValues = {"FIXED", "PERCENTILE", "RANDOM", "NORMAL_DISTRIBUTION"}
        )
        @Pattern(regexp = "^(FIXED|PERCENTILE|RANDOM|NORMAL_DISTRIBUTION)$", message = "Invalid latency type")
        private String type = "FIXED";
        
        @Schema(description = "Fixed latency in milliseconds (used when type is FIXED)", example = "200")
        @Min(value = 0, message = "Fixed latency must be non-negative")
        private Long fixedLatencyMs;
        
        @Schema(description = "P50 latency in milliseconds", example = "100")
        @Min(value = 0, message = "P50 latency must be non-negative")
        private Long p50;
        
        @Schema(description = "P95 latency in milliseconds", example = "500")
        @Min(value = 0, message = "P95 latency must be non-negative")
        private Long p95;
        
        @Schema(description = "P99 latency in milliseconds", example = "1000")
        @Min(value = 0, message = "P99 latency must be non-negative")
        private Long p99;
        
        @Schema(description = "P99.9 latency in milliseconds", example = "2000")
        @Min(value = 0, message = "P999 latency must be non-negative")
        private Long p999;
        
        @Schema(description = "Minimum latency for random distribution", example = "50")
        @Min(value = 0, message = "Min latency must be non-negative")
        private Long minLatencyMs;
        
        @Schema(description = "Maximum latency for random distribution", example = "1000")
        @Min(value = 0, message = "Max latency must be non-negative")
        private Long maxLatencyMs;
        
        @Schema(description = "Mean latency for normal distribution", example = "200")
        @Min(value = 0, message = "Mean latency must be non-negative")
        private Double meanLatencyMs;
        
        @Schema(description = "Standard deviation for normal distribution", example = "50.0")
        @Min(value = 0, message = "Standard deviation must be non-negative")
        private Double stdDeviationMs;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @Schema(description = "Timeout configuration settings")
    public static class TimeoutConfig {
        
        @Schema(description = "Whether timeout simulation is enabled", example = "true")
        private Boolean enabled = false;
        
        @Schema(description = "Timeout duration in milliseconds", example = "5000")
        @Min(value = 1, message = "Timeout must be at least 1ms")
        private Long timeoutMs = 5000L;
        
        @Schema(description = "Probability of timeout occurring (0.0 to 1.0)", example = "0.01")
        @DecimalMin(value = "0.0", message = "Timeout probability must be between 0 and 1")
        @DecimalMax(value = "1.0", message = "Timeout probability must be between 0 and 1")
        private Double timeoutProbability = 0.01;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @Schema(description = "Error configuration settings")
    public static class ErrorConfig {
        
        @Schema(description = "Whether error simulation is enabled", example = "true")
        private Boolean enabled = false;
        
        @Schema(description = "Error rate (0.0 to 1.0)", example = "0.05")
        @DecimalMin(value = "0.0", message = "Error rate must be between 0 and 1")
        @DecimalMax(value = "1.0", message = "Error rate must be between 0 and 1")
        private Double errorRate = 0.05;
        
        @Schema(description = "List of error status codes to return", example = "[500, 502, 503]")
        private Integer[] errorStatusCodes = {500, 502, 503};
        
        @Schema(description = "Custom error messages for different status codes")
        private Map<Integer, String> errorMessages;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @Schema(description = "Rate limiting configuration settings")
    public static class RateLimitConfig {
        
        @Schema(description = "Whether rate limiting is enabled", example = "true")
        private Boolean enabled = false;
        
        @Schema(description = "Requests per second limit", example = "100")
        @Min(value = 1, message = "Requests per second must be at least 1")
        private Integer requestsPerSecond = 100;
        
        @Schema(description = "Burst size for rate limiting", example = "10")
        @Min(value = 1, message = "Burst size must be at least 1")
        private Integer burstSize = 10;
        
        @Schema(description = "Rate limit exceeded status code", example = "429")
        @Min(value = 400, message = "Rate limit status code must be 4xx or 5xx")
        @Max(value = 599, message = "Rate limit status code must be 4xx or 5xx")
        private Integer rateLimitStatusCode = 429;
    }
} 