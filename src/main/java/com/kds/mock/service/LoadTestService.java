package com.kds.mock.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kds.mock.dto.LoadTestConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.PreDestroy;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Service
@Slf4j
public class LoadTestService {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Random random = new Random();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);
    
    // Rate limiting tracking per endpoint
    private final ConcurrentHashMap<String, RateLimitTracker> rateLimitTrackers = new ConcurrentHashMap<>();
    
    @PreDestroy
    public void shutdown() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Simulates latency based on the configured load test settings
     */
    public void simulateLatency(LoadTestConfig config) throws InterruptedException {
        if (config == null || config.getLatencyConfig() == null) {
            return;
        }
        
        LoadTestConfig.LatencyConfig latencyConfig = config.getLatencyConfig();
        long latencyMs = calculateLatency(latencyConfig);
        
        if (latencyMs > 0) {
            Thread.sleep(latencyMs);
        }
    }
    
    /**
     * Checks if a timeout should occur based on configuration
     */
    public boolean shouldTimeout(LoadTestConfig config) {
        if (config == null || config.getTimeoutConfig() == null || !config.getTimeoutConfig().getEnabled()) {
            return false;
        }
        
        LoadTestConfig.TimeoutConfig timeoutConfig = config.getTimeoutConfig();
        return random.nextDouble() < timeoutConfig.getTimeoutProbability();
    }
    
    /**
     * Checks if an error should be generated based on configuration
     */
    public boolean shouldGenerateError(LoadTestConfig config) {
        if (config == null || config.getErrorConfig() == null || !config.getErrorConfig().getEnabled()) {
            return false;
        }
        
        LoadTestConfig.ErrorConfig errorConfig = config.getErrorConfig();
        return random.nextDouble() < errorConfig.getErrorRate();
    }
    
    /**
     * Gets a random error status code from the configuration
     */
    public int getRandomErrorStatusCode(LoadTestConfig config) {
        if (config == null || config.getErrorConfig() == null) {
            return 500;
        }
        
        LoadTestConfig.ErrorConfig errorConfig = config.getErrorConfig();
        Integer[] statusCodes = errorConfig.getErrorStatusCodes();
        
        if (statusCodes == null || statusCodes.length == 0) {
            return 500;
        }
        
        return statusCodes[random.nextInt(statusCodes.length)];
    }
    
    /**
     * Gets a custom error message for the given status code
     */
    public String getErrorMessage(LoadTestConfig config, int statusCode) {
        if (config == null || config.getErrorConfig() == null || config.getErrorConfig().getErrorMessages() == null) {
            return getDefaultErrorMessage(statusCode);
        }
        
        LoadTestConfig.ErrorConfig errorConfig = config.getErrorConfig();
        String customMessage = errorConfig.getErrorMessages().get(statusCode);
        
        return customMessage != null ? customMessage : getDefaultErrorMessage(statusCode);
    }
    
    /**
     * Checks if rate limit is exceeded for the given endpoint
     */
    public boolean isRateLimitExceeded(String endpointPath, LoadTestConfig config) {
        if (config == null || config.getRateLimitConfig() == null || !config.getRateLimitConfig().getEnabled()) {
            return false;
        }
        
        LoadTestConfig.RateLimitConfig rateLimitConfig = config.getRateLimitConfig();
        RateLimitTracker tracker = rateLimitTrackers.computeIfAbsent(endpointPath, k -> new RateLimitTracker());
        
        return tracker.isRateLimitExceeded(rateLimitConfig.getRequestsPerSecond(), rateLimitConfig.getBurstSize(), scheduler);
    }
    
    /**
     * Parses load test configuration from JSON string
     */
    public LoadTestConfig parseLoadTestConfig(String configJson) {
        if (configJson == null || configJson.trim().isEmpty()) {
            return null;
        }
        
        try {
            return objectMapper.readValue(configJson, LoadTestConfig.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid load test configuration JSON: " + e.getMessage());
        }
    }
    
    /**
     * Converts load test configuration to JSON string
     */
    public String toJson(LoadTestConfig config) {
        if (config == null) {
            return null;
        }
        
        try {
            return objectMapper.writeValueAsString(config);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize load test configuration", e);
        }
    }
    
    private long calculateLatency(LoadTestConfig.LatencyConfig config) {
        return switch (config.getType()) {
            case "FIXED" -> config.getFixedLatencyMs() != null ? config.getFixedLatencyMs() : 0;
            case "RANDOM" -> calculateRandomLatency(config);
            case "NORMAL_DISTRIBUTION" -> calculateNormalDistributionLatency(config);
            case "PERCENTILE" -> calculatePercentileLatency(config);
            default -> 0;
        };
    }

    private long calculateRandomLatency(LoadTestConfig.LatencyConfig config) {
        long min = config.getMinLatencyMs() != null ? config.getMinLatencyMs() : 0;
        long max = config.getMaxLatencyMs() != null ? config.getMaxLatencyMs() : 1000;
        return min + (long) (random.nextDouble() * (max - min));
    }

    private long calculateNormalDistributionLatency(LoadTestConfig.LatencyConfig config) {
        double mean = config.getMeanLatencyMs() != null ? config.getMeanLatencyMs() : 200.0;
        double stdDev = config.getStdDeviationMs() != null ? config.getStdDeviationMs() : 50.0;
        double value = random.nextGaussian() * stdDev + mean;
        return Math.max(0, Math.round(value));
    }
    
    private long calculatePercentileLatency(LoadTestConfig.LatencyConfig config) {
        double percentile = random.nextDouble() * 100;
        
        if (percentile <= 50 && config.getP50() != null) {
            return config.getP50();
        } else if (percentile <= 95 && config.getP95() != null) {
            return config.getP95();
        } else if (percentile <= 99 && config.getP99() != null) {
            return config.getP99();
        } else if (percentile <= 99.9 && config.getP999() != null) {
            return config.getP999();
        } else {
            // Default to P99 if available, otherwise 1000ms
            return config.getP99() != null ? config.getP99() : 1000;
        }
    }
    
    private String getDefaultErrorMessage(int statusCode) {
        return switch (statusCode) {
            case 400 -> "Bad Request";
            case 401 -> "Unauthorized";
            case 403 -> "Forbidden";
            case 404 -> "Not Found";
            case 429 -> "Too Many Requests";
            case 500 -> "Internal Server Error";
            case 502 -> "Bad Gateway";
            case 503 -> "Service Unavailable";
            case 504 -> "Gateway Timeout";
            default -> "Error occurred";
        };
    }
    
    /**
     * Rate limiting tracker for individual endpoints
     */
    private static class RateLimitTracker {
        private final AtomicInteger currentRequests = new AtomicInteger(0);
        private final AtomicLong windowStartTime = new AtomicLong(System.currentTimeMillis());
        private final AtomicInteger windowRequestCount = new AtomicInteger(0);
        
        public boolean isRateLimitExceeded(int requestsPerSecond, int burstSize, ScheduledExecutorService scheduler) {
            long currentTime = System.currentTimeMillis();
            long windowStart = windowStartTime.get();
            
            // Reset window if 1 second has passed
            if (currentTime - windowStart >= 1000) {
                windowStartTime.set(currentTime);
                windowRequestCount.set(0);
            }
            
            // Check burst limit
            if (currentRequests.get() >= burstSize) {
                return true;
            }
            
            // Check rate limit
            if (windowRequestCount.get() >= requestsPerSecond) {
                return true;
            }
            
            // Increment counters
            currentRequests.incrementAndGet();
            windowRequestCount.incrementAndGet();
            
            // Schedule decrement after 1 second using the shared scheduler
            scheduler.schedule(() -> currentRequests.decrementAndGet(), 1, TimeUnit.SECONDS);
            
            return false;
        }
    }
} 