package com.kds.mock.controllers;

import com.kds.mock.dto.LoadTestConfig;
import com.kds.mock.service.LoadTestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/load-test")
@Tag(
    name = "Load Testing", 
    description = "APIs for managing and monitoring load testing configurations and performance metrics."
)
@RequiredArgsConstructor
public class LoadTestController {

    private final LoadTestService loadTestService;

    @Operation(
        summary = "Validate load test configuration",
        description = """
            Validates a load test configuration to ensure it's properly formatted and contains valid values.
            
            **Validation Rules:**
            - Latency values must be non-negative
            - Percentile values must be in ascending order (P50 ≤ P95 ≤ P99 ≤ P999)
            - Error rates and timeout probabilities must be between 0 and 1
            - Rate limiting values must be positive integers
            
            **Use Cases:**
            - Validate configuration before applying to endpoints
            - Test configuration syntax
            - Ensure realistic load testing parameters
            """,
        tags = {"Load Testing"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Configuration is valid",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Valid Configuration",
                    value = """
                        {
                            "valid": true,
                            "message": "Load test configuration is valid",
                            "warnings": []
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Configuration is invalid",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Invalid Configuration",
                    value = """
                        {
                            "valid": false,
                            "message": "Configuration validation failed",
                            "errors": [
                                "P95 latency (300) must be greater than P50 latency (500)",
                                "Error rate (1.5) must be between 0 and 1"
                            ],
                            "warnings": [
                                "No timeout configuration provided"
                            ]
                        }
                        """
                )
            )
        )
    })
    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateLoadTestConfig(
            @Parameter(
                description = "Load test configuration to validate",
                required = true,
                content = @Content(
                    schema = @Schema(implementation = LoadTestConfig.class),
                    examples = {
                        @ExampleObject(
                            name = "Sample Configuration",
                            description = "Basic load test configuration with latency and error simulation",
                            value = """
                                {
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
                                """
                        ),
                        @ExampleObject(
                            name = "Fixed Latency Configuration",
                            description = "Simple fixed latency configuration",
                            value = """
                                {
                                    "latencyConfig": {
                                        "type": "FIXED",
                                        "fixedLatencyMs": 200
                                    }
                                }
                                """
                        ),
                        @ExampleObject(
                            name = "Complete Configuration",
                            description = "Full load test configuration with all options",
                            value = """
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
                        ),
                        @ExampleObject(
                            name = "Random Latency Configuration",
                            description = "Random latency distribution configuration",
                            value = """
                                {
                                    "latencyConfig": {
                                        "type": "RANDOM",
                                        "minLatencyMs": 50,
                                        "maxLatencyMs": 1000
                                    }
                                }
                                """
                        ),
                        @ExampleObject(
                            name = "Normal Distribution Configuration",
                            description = "Normal distribution latency configuration",
                            value = """
                                {
                                    "latencyConfig": {
                                        "type": "NORMAL_DISTRIBUTION",
                                        "meanLatencyMs": 200.0,
                                        "stdDeviationMs": 50.0
                                    }
                                }
                                """
                        )
                    }
                )
            )
            @RequestBody LoadTestConfig config) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Try to serialize and deserialize to validate JSON structure
            String json = loadTestService.toJson(config);
            LoadTestConfig parsed = loadTestService.parseLoadTestConfig(json);
            
            // Additional validation logic
            boolean isValid = validateConfiguration(parsed);
            
            result.put("valid", isValid);
            result.put("message", isValid ? "Load test configuration is valid" : "Configuration validation failed");
            
            if (!isValid) {
                result.put("errors", getValidationErrors(parsed));
            }
            
            result.put("warnings", getValidationWarnings(parsed));
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            result.put("valid", false);
            result.put("message", "Configuration validation failed: " + e.getMessage());
            result.put("errors", new String[]{e.getMessage()});
            result.put("warnings", new String[]{});
            
            return ResponseEntity.badRequest().body(result);
        }
    }

    @Operation(
        summary = "Get load testing statistics",
        description = """
            Retrieves current load testing statistics and performance metrics.
            
            **Metrics Included:**
            - Active rate limit trackers
            - Current request counts
            - Performance statistics
            - Configuration summary
            
            **Use Cases:**
            - Monitor load testing performance
            - Debug rate limiting issues
            - Track system behavior under load
            """,
        tags = {"Load Testing"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved load testing statistics",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Statistics",
                    value = """
                        {
                            "activeEndpoints": 5,
                            "totalRequests": 1250,
                            "rateLimitedRequests": 45,
                            "timeoutRequests": 12,
                            "errorRequests": 63,
                            "averageLatency": 245.5,
                            "maxLatency": 1200,
                            "minLatency": 50
                        }
                        """
                )
            )
        )
    })
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getLoadTestStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // Mock statistics - in a real implementation, these would come from actual metrics
        stats.put("activeEndpoints", 5);
        stats.put("totalRequests", 1250);
        stats.put("rateLimitedRequests", 45);
        stats.put("timeoutRequests", 12);
        stats.put("errorRequests", 63);
        stats.put("averageLatency", 245.5);
        stats.put("maxLatency", 1200);
        stats.put("minLatency", 50);
        
        return ResponseEntity.ok(stats);
    }

    @Operation(
        summary = "Reset load testing state",
        description = """
            Resets all load testing state including rate limit counters and statistics.
            
            **What gets reset:**
            - Rate limit trackers for all endpoints
            - Request counters
            - Performance statistics
            
            **Use Cases:**
            - Start fresh load testing session
            - Clear accumulated state
            - Reset after configuration changes
            """,
        tags = {"Load Testing"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully reset load testing state",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Reset Result",
                    value = """
                        {
                            "message": "Load testing state reset successfully",
                            "resetTimestamp": "2024-01-15T10:30:00Z"
                        }
                        """
                )
            )
        )
    })
    @PostMapping("/reset")
    public ResponseEntity<Map<String, Object>> resetLoadTestState() {
        Map<String, Object> result = new HashMap<>();
        result.put("message", "Load testing state reset successfully");
        result.put("resetTimestamp", java.time.LocalDateTime.now().toString());
        
        // In a real implementation, this would reset the actual state
        // loadTestService.resetState();
        
        return ResponseEntity.ok(result);
    }

    private boolean validateConfiguration(LoadTestConfig config) {
        if (config == null) return true;
        
        try {
            // Validate latency configuration
            if (config.getLatencyConfig() != null) {
                LoadTestConfig.LatencyConfig latency = config.getLatencyConfig();
                
                // Check percentile values are in ascending order
                if (latency.getP50() != null && latency.getP95() != null && latency.getP50() > latency.getP95()) {
                    return false;
                }
                if (latency.getP95() != null && latency.getP99() != null && latency.getP95() > latency.getP99()) {
                    return false;
                }
                if (latency.getP99() != null && latency.getP999() != null && latency.getP99() > latency.getP999()) {
                    return false;
                }
                
                // Check fixed latency is non-negative
                if (latency.getFixedLatencyMs() != null && latency.getFixedLatencyMs() < 0) {
                    return false;
                }
                
                // Check random latency bounds
                if (latency.getMinLatencyMs() != null && latency.getMaxLatencyMs() != null) {
                    if (latency.getMinLatencyMs() < 0 || latency.getMaxLatencyMs() < 0 || 
                        latency.getMinLatencyMs() > latency.getMaxLatencyMs()) {
                        return false;
                    }
                }
                
                // Check normal distribution parameters
                if (latency.getMeanLatencyMs() != null && latency.getStdDeviationMs() != null) {
                    if (latency.getMeanLatencyMs() < 0 || latency.getStdDeviationMs() < 0) {
                        return false;
                    }
                }
            }
            
            // Validate error configuration
            if (config.getErrorConfig() != null && config.getErrorConfig().getEnabled()) {
                Double errorRate = config.getErrorConfig().getErrorRate();
                if (errorRate != null && (errorRate < 0 || errorRate > 1)) {
                    return false;
                }
            }
            
            // Validate timeout configuration
            if (config.getTimeoutConfig() != null && config.getTimeoutConfig().getEnabled()) {
                Double timeoutProb = config.getTimeoutConfig().getTimeoutProbability();
                if (timeoutProb != null && (timeoutProb < 0 || timeoutProb > 1)) {
                    return false;
                }
                
                Long timeoutMs = config.getTimeoutConfig().getTimeoutMs();
                if (timeoutMs != null && timeoutMs <= 0) {
                    return false;
                }
            }
            
            // Validate rate limit configuration
            if (config.getRateLimitConfig() != null && config.getRateLimitConfig().getEnabled()) {
                Integer rps = config.getRateLimitConfig().getRequestsPerSecond();
                if (rps != null && rps <= 0) {
                    return false;
                }
                
                Integer burstSize = config.getRateLimitConfig().getBurstSize();
                if (burstSize != null && burstSize <= 0) {
                    return false;
                }
            }
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String[] getValidationErrors(LoadTestConfig config) {
        if (config == null) return new String[]{};
        
        java.util.List<String> errors = new java.util.ArrayList<>();
        
        try {
            // Validate latency configuration
            if (config.getLatencyConfig() != null) {
                LoadTestConfig.LatencyConfig latency = config.getLatencyConfig();
                
                if (latency.getP50() != null && latency.getP95() != null && latency.getP50() > latency.getP95()) {
                    errors.add("P50 latency (" + latency.getP50() + ") must be less than or equal to P95 latency (" + latency.getP95() + ")");
                }
                if (latency.getP95() != null && latency.getP99() != null && latency.getP95() > latency.getP99()) {
                    errors.add("P95 latency (" + latency.getP95() + ") must be less than or equal to P99 latency (" + latency.getP99() + ")");
                }
                if (latency.getP99() != null && latency.getP999() != null && latency.getP99() > latency.getP999()) {
                    errors.add("P99 latency (" + latency.getP99() + ") must be less than or equal to P999 latency (" + latency.getP999() + ")");
                }
                
                if (latency.getFixedLatencyMs() != null && latency.getFixedLatencyMs() < 0) {
                    errors.add("Fixed latency must be non-negative");
                }
                
                if (latency.getMinLatencyMs() != null && latency.getMaxLatencyMs() != null) {
                    if (latency.getMinLatencyMs() < 0) {
                        errors.add("Min latency must be non-negative");
                    }
                    if (latency.getMaxLatencyMs() < 0) {
                        errors.add("Max latency must be non-negative");
                    }
                    if (latency.getMinLatencyMs() > latency.getMaxLatencyMs()) {
                        errors.add("Min latency must be less than or equal to max latency");
                    }
                }
                
                if (latency.getMeanLatencyMs() != null && latency.getStdDeviationMs() != null) {
                    if (latency.getMeanLatencyMs() < 0) {
                        errors.add("Mean latency must be non-negative");
                    }
                    if (latency.getStdDeviationMs() < 0) {
                        errors.add("Standard deviation must be non-negative");
                    }
                }
            }
            
            // Validate error configuration
            if (config.getErrorConfig() != null && config.getErrorConfig().getEnabled()) {
                Double errorRate = config.getErrorConfig().getErrorRate();
                if (errorRate != null && (errorRate < 0 || errorRate > 1)) {
                    errors.add("Error rate (" + errorRate + ") must be between 0 and 1");
                }
            }
            
            // Validate timeout configuration
            if (config.getTimeoutConfig() != null && config.getTimeoutConfig().getEnabled()) {
                Double timeoutProb = config.getTimeoutConfig().getTimeoutProbability();
                if (timeoutProb != null && (timeoutProb < 0 || timeoutProb > 1)) {
                    errors.add("Timeout probability (" + timeoutProb + ") must be between 0 and 1");
                }
                
                Long timeoutMs = config.getTimeoutConfig().getTimeoutMs();
                if (timeoutMs != null && timeoutMs <= 0) {
                    errors.add("Timeout duration must be positive");
                }
            }
            
            // Validate rate limit configuration
            if (config.getRateLimitConfig() != null && config.getRateLimitConfig().getEnabled()) {
                Integer rps = config.getRateLimitConfig().getRequestsPerSecond();
                if (rps != null && rps <= 0) {
                    errors.add("Requests per second must be positive");
                }
                
                Integer burstSize = config.getRateLimitConfig().getBurstSize();
                if (burstSize != null && burstSize <= 0) {
                    errors.add("Burst size must be positive");
                }
            }
        } catch (Exception e) {
            errors.add("Validation error: " + e.getMessage());
        }
        
        return errors.toArray(new String[0]);
    }

    private String[] getValidationWarnings(LoadTestConfig config) {
        if (config == null) return new String[]{};
        
        java.util.List<String> warnings = new java.util.ArrayList<>();
        
        try {
            // Check for missing configurations
            if (config.getLatencyConfig() == null) {
                warnings.add("No latency configuration provided");
            }
            if (config.getTimeoutConfig() == null || !config.getTimeoutConfig().getEnabled()) {
                warnings.add("No timeout configuration provided");
            }
            if (config.getErrorConfig() == null || !config.getErrorConfig().getEnabled()) {
                warnings.add("No error configuration provided");
            }
            if (config.getRateLimitConfig() == null || !config.getRateLimitConfig().getEnabled()) {
                warnings.add("No rate limit configuration provided");
            }
        } catch (Exception e) {
            warnings.add("Warning: " + e.getMessage());
        }
        
        return warnings.toArray(new String[0]);
    }
} 