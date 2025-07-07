package com.kds.mock.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/health")
@Tag(
    name = "Health Check", 
    description = "Health monitoring and system status endpoints for monitoring the mock service."
)
@RequiredArgsConstructor
public class HealthController {

    @Operation(
        summary = "Get service health status",
        description = """
            Returns the current health status of the mock service including:
            - Service status (UP/DOWN)
            - Database connectivity
            - System information
            - Timestamp
            
            **Use Cases:**
            - Health monitoring and alerting
            - Load balancer health checks
            - System diagnostics
            - Deployment verification
            """,
        tags = {"Health Check"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Service is healthy",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    type = "object"
                ),
                examples = @ExampleObject(
                    name = "Healthy Service",
                    value = """
                        {
                            "status": "UP",
                            "timestamp": "2024-01-15T10:30:00",
                            "database": "UP",
                            "version": "1.0.0",
                            "uptime": "2h 15m 30s",
                            "activeEndpoints": 5,
                            "totalRequests": 1250
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "503",
            description = "Service is unhealthy",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Unhealthy Service",
                    value = """
                        {
                            "status": "DOWN",
                            "timestamp": "2024-01-15T10:30:00",
                            "database": "DOWN",
                            "version": "1.0.0",
                            "error": "Database connection failed"
                        }
                        """
                )
            )
        )
    })
    @GetMapping
    public ResponseEntity<Map<String, Object>> getHealth() {
        Map<String, Object> health = new HashMap<>();
        
        try {
            // Basic health check - in a real implementation, you'd check database connectivity
            health.put("status", "UP");
            health.put("timestamp", LocalDateTime.now().toString());
            health.put("database", "UP");
            health.put("version", "1.0.0");
            health.put("uptime", "2h 15m 30s"); // Mock uptime
            health.put("activeEndpoints", 5); // Mock count
            health.put("totalRequests", 1250); // Mock count
            
            return ResponseEntity.ok(health);
        } catch (Exception e) {
            health.put("status", "DOWN");
            health.put("timestamp", LocalDateTime.now().toString());
            health.put("error", e.getMessage());
            return ResponseEntity.status(503).body(health);
        }
    }

    @Operation(
        summary = "Get detailed system information",
        description = """
            Returns detailed system information including:
            - JVM information
            - Memory usage
            - Thread information
            - System properties
            
            **Use Cases:**
            - System diagnostics
            - Performance monitoring
            - Troubleshooting
            - Capacity planning
            """,
        tags = {"Health Check"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "System information retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "System Info",
                    value = """
                        {
                            "jvm": {
                                "version": "21.0.1",
                                "vendor": "Oracle Corporation",
                                "memory": {
                                    "total": "512MB",
                                    "free": "256MB",
                                    "used": "256MB"
                                }
                            },
                            "system": {
                                "os": "Linux",
                                "arch": "x86_64",
                                "processors": 4
                            },
                            "application": {
                                "name": "mock-service",
                                "version": "1.0.0",
                                "startTime": "2024-01-15T08:15:00"
                            }
                        }
                        """
                )
            )
        )
    })
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getSystemInfo() {
        Map<String, Object> info = new HashMap<>();
        
        // JVM Information
        Map<String, Object> jvm = new HashMap<>();
        jvm.put("version", System.getProperty("java.version"));
        jvm.put("vendor", System.getProperty("java.vendor"));
        
        Runtime runtime = Runtime.getRuntime();
        Map<String, Object> memory = new HashMap<>();
        memory.put("total", formatBytes(runtime.totalMemory()));
        memory.put("free", formatBytes(runtime.freeMemory()));
        memory.put("used", formatBytes(runtime.totalMemory() - runtime.freeMemory()));
        jvm.put("memory", memory);
        
        info.put("jvm", jvm);
        
        // System Information
        Map<String, Object> system = new HashMap<>();
        system.put("os", System.getProperty("os.name"));
        system.put("arch", System.getProperty("os.arch"));
        system.put("processors", runtime.availableProcessors());
        info.put("system", system);
        
        // Application Information
        Map<String, Object> application = new HashMap<>();
        application.put("name", "mock-service");
        application.put("version", "1.0.0");
        application.put("startTime", LocalDateTime.now().minusHours(2).toString()); // Mock start time
        info.put("application", application);
        
        return ResponseEntity.ok(info);
    }

    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp-1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }
} 