package com.kds.mock.service;

import com.kds.mock.dto.LoadTestConfig;
import com.kds.mock.dto.LoadTestConfig.ErrorConfig;
import com.kds.mock.dto.LoadTestConfig.LatencyConfig;
import com.kds.mock.dto.LoadTestConfig.RateLimitConfig;
import com.kds.mock.dto.LoadTestConfig.TimeoutConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class LoadTestServiceTests {

    private LoadTestService loadTestService;

    @BeforeEach
    void setUp() {
        loadTestService = new LoadTestService();
    }

    private void setField(Object obj, String fieldName, Object value) {
        try {
            Class<?> clazz = obj.getClass();
            java.lang.reflect.Field field = null;
            
            while (clazz != null && field == null) {
                try {
                    field = clazz.getDeclaredField(fieldName);
                } catch (NoSuchFieldException e) {
                    clazz = clazz.getSuperclass();
                }
            }
            
            if (field == null) {
                throw new NoSuchFieldException("Field " + fieldName + " not found in class hierarchy");
            }
            
            field.setAccessible(true);
            field.set(obj, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Object invokePrivateMethod(Object obj, String methodName, Object... args) {
        try {
            Class<?> clazz = obj.getClass();
            Method method = null;
            
            // Find method with matching parameter types
            for (Method m : clazz.getDeclaredMethods()) {
                if (m.getName().equals(methodName) && m.getParameterCount() == args.length) {
                    method = m;
                    break;
                }
            }
            
            if (method == null) {
                throw new NoSuchMethodException("Method " + methodName + " not found");
            }
            
            method.setAccessible(true);
            return method.invoke(obj, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Object getField(Object obj, String fieldName) {
        try {
            Class<?> clazz = obj.getClass();
            java.lang.reflect.Field field = null;
            
            while (clazz != null && field == null) {
                try {
                    field = clazz.getDeclaredField(fieldName);
                } catch (NoSuchFieldException e) {
                    clazz = clazz.getSuperclass();
                }
            }
            
            if (field == null) {
                throw new NoSuchFieldException("Field " + fieldName + " not found in class hierarchy");
            }
            
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testShutdown() {
        // When
        loadTestService.shutdown();

        // Then - should not throw exception
        assertDoesNotThrow(() -> loadTestService.shutdown());
    }

    @Test
    void testSimulateLatency_WithLatencyConfig() throws InterruptedException {
        // Given
        LoadTestConfig config = new LoadTestConfig();
        LatencyConfig latencyConfig = new LatencyConfig();
        setField(latencyConfig, "type", "FIXED");
        setField(latencyConfig, "fixedLatencyMs", 100L);
        setField(config, "latencyConfig", latencyConfig);

        // When
        long startTime = System.currentTimeMillis();
        loadTestService.simulateLatency(config);
        long endTime = System.currentTimeMillis();

        // Then
        long actualLatency = endTime - startTime;
        assertTrue(actualLatency >= 100, "Latency should be at least 100ms");
    }

    @Test
    void testSimulateLatency_WithoutLatencyConfig() throws InterruptedException {
        // Given
        LoadTestConfig config = new LoadTestConfig();

        // When
        long startTime = System.currentTimeMillis();
        loadTestService.simulateLatency(config);
        long endTime = System.currentTimeMillis();

        // Then
        long actualLatency = endTime - startTime;
        assertTrue(actualLatency < 50, "Should have minimal latency without config");
    }

    @Test
    void testShouldTimeout_WithTimeoutConfig() {
        // Given
        LoadTestConfig config = new LoadTestConfig();
        TimeoutConfig timeoutConfig = new TimeoutConfig();
        setField(timeoutConfig, "enabled", true);
        setField(timeoutConfig, "timeoutProbability", 1.0); // 100% probability
        setField(config, "timeoutConfig", timeoutConfig);

        // When
        boolean shouldTimeout = loadTestService.shouldTimeout(config);

        // Then
        assertTrue(shouldTimeout);
    }

    @Test
    void testShouldTimeout_WithoutTimeoutConfig() {
        // Given
        LoadTestConfig config = new LoadTestConfig();

        // When
        boolean shouldTimeout = loadTestService.shouldTimeout(config);

        // Then
        assertFalse(shouldTimeout);
    }

    @Test
    void testShouldTimeout_Disabled() {
        // Given
        LoadTestConfig config = new LoadTestConfig();
        TimeoutConfig timeoutConfig = new TimeoutConfig();
        setField(timeoutConfig, "enabled", false);
        setField(config, "timeoutConfig", timeoutConfig);

        // When
        boolean shouldTimeout = loadTestService.shouldTimeout(config);

        // Then
        assertFalse(shouldTimeout);
    }

    @Test
    void testShouldGenerateError_WithErrorConfig() {
        // Given
        LoadTestConfig config = new LoadTestConfig();
        ErrorConfig errorConfig = new ErrorConfig();
        setField(errorConfig, "enabled", true);
        setField(errorConfig, "errorRate", 1.0); // 100% error rate
        setField(config, "errorConfig", errorConfig);

        // When
        boolean shouldGenerateError = loadTestService.shouldGenerateError(config);

        // Then
        assertTrue(shouldGenerateError);
    }

    @Test
    void testShouldGenerateError_WithoutErrorConfig() {
        // Given
        LoadTestConfig config = new LoadTestConfig();

        // When
        boolean shouldGenerateError = loadTestService.shouldGenerateError(config);

        // Then
        assertFalse(shouldGenerateError);
    }

    @Test
    void testShouldGenerateError_Disabled() {
        // Given
        LoadTestConfig config = new LoadTestConfig();
        ErrorConfig errorConfig = new ErrorConfig();
        setField(errorConfig, "enabled", false);
        setField(config, "errorConfig", errorConfig);

        // When
        boolean shouldGenerateError = loadTestService.shouldGenerateError(config);

        // Then
        assertFalse(shouldGenerateError);
    }

    @Test
    void testGetRandomErrorStatusCode_WithErrorConfig() {
        // Given
        LoadTestConfig config = new LoadTestConfig();
        ErrorConfig errorConfig = new ErrorConfig();
        setField(errorConfig, "errorStatusCodes", new Integer[]{500, 502, 503});
        setField(config, "errorConfig", errorConfig);

        // When
        int statusCode = loadTestService.getRandomErrorStatusCode(config);

        // Then
        assertTrue(Arrays.asList(500, 502, 503).contains(statusCode));
    }

    @Test
    void testGetRandomErrorStatusCode_WithoutErrorConfig() {
        // Given
        LoadTestConfig config = new LoadTestConfig();

        // When
        int statusCode = loadTestService.getRandomErrorStatusCode(config);

        // Then
        assertEquals(500, statusCode); // Default error code
    }

    @Test
    void testGetErrorMessage_WithErrorConfig() {
        // Given
        LoadTestConfig config = new LoadTestConfig();
        ErrorConfig errorConfig = new ErrorConfig();
        setField(errorConfig, "errorMessages", java.util.Map.of(500, "Custom error message"));
        setField(config, "errorConfig", errorConfig);

        // When
        String message = loadTestService.getErrorMessage(config, 500);

        // Then
        assertEquals("Custom error message", message);
    }

    @Test
    void testGetErrorMessage_WithoutErrorConfig() {
        // Given
        LoadTestConfig config = new LoadTestConfig();

        // When
        String message = loadTestService.getErrorMessage(config, 500);

        // Then
        assertNotNull(message);
        assertFalse(message.isEmpty());
    }

    @Test
    void testIsRateLimitExceeded_WithoutRateLimitConfig() {
        // Given
        LoadTestConfig config = new LoadTestConfig();

        // When
        boolean exceeded = loadTestService.isRateLimitExceeded("test-path", config);

        // Then
        assertFalse(exceeded);
    }

    @Test
    void testParseLoadTestConfig_ValidJson() {
        // Given
        String json = "{\"latencyConfig\":{\"type\":\"FIXED\",\"fixedLatencyMs\":100}}";

        // When
        LoadTestConfig config = loadTestService.parseLoadTestConfig(json);

        // Then
        assertNotNull(config);
        LatencyConfig latencyConfig = (LatencyConfig) getField(config, "latencyConfig");
        assertNotNull(latencyConfig);
        assertEquals("FIXED", getField(latencyConfig, "type"));
        assertEquals(100L, getField(latencyConfig, "fixedLatencyMs"));
    }

    @Test
    void testParseLoadTestConfig_InvalidJson() {
        // Given
        String invalidJson = "invalid json";

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> loadTestService.parseLoadTestConfig(invalidJson));
    }

    @Test
    void testParseLoadTestConfig_NullJson() {
        // When
        LoadTestConfig config = loadTestService.parseLoadTestConfig(null);

        // Then
        assertNull(config);
    }

    @Test
    void testToJson_ValidConfig() {
        // Given
        LoadTestConfig config = new LoadTestConfig();
        LatencyConfig latencyConfig = new LatencyConfig();
        setField(latencyConfig, "type", "FIXED");
        setField(latencyConfig, "fixedLatencyMs", 100L);
        setField(config, "latencyConfig", latencyConfig);

        // When
        String json = loadTestService.toJson(config);

        // Then
        assertNotNull(json);
        assertTrue(json.contains("FIXED"));
        assertTrue(json.contains("100"));
    }

    @Test
    void testToJson_NullConfig() {
        // When
        String json = loadTestService.toJson(null);

        // Then
        assertNull(json);
    }

    @Test
    void testCalculateLatency_FixedType() {
        // Given
        LatencyConfig config = new LatencyConfig();
        setField(config, "type", "FIXED");
        setField(config, "fixedLatencyMs", 200L);

        // When
        long latency = (Long) invokePrivateMethod(loadTestService, "calculateLatency", config);

        // Then
        assertEquals(200L, latency);
    }

    @Test
    void testCalculateLatency_RandomType() {
        // Given
        LatencyConfig config = new LatencyConfig();
        setField(config, "type", "RANDOM");
        setField(config, "minLatencyMs", 100L);
        setField(config, "maxLatencyMs", 300L);

        // When
        long latency = (Long) invokePrivateMethod(loadTestService, "calculateLatency", config);

        // Then
        assertTrue(latency >= 100L && latency <= 300L);
    }

    @Test
    void testCalculateLatency_NormalDistributionType() {
        // Given
        LatencyConfig config = new LatencyConfig();
        setField(config, "type", "NORMAL_DISTRIBUTION");
        setField(config, "meanLatencyMs", 200.0);
        setField(config, "stdDeviationMs", 50.0);

        // When
        long latency = (Long) invokePrivateMethod(loadTestService, "calculateLatency", config);

        // Then
        assertTrue(latency > 0L);
    }

    @Test
    void testCalculateLatency_PercentileType() {
        // Given
        LatencyConfig config = new LatencyConfig();
        setField(config, "type", "PERCENTILE");
        setField(config, "p50", 100L);
        setField(config, "p95", 200L);
        setField(config, "p99", 300L);

        // When
        long latency = (Long) invokePrivateMethod(loadTestService, "calculateLatency", config);

        // Then
        assertTrue(latency > 0L);
    }

    @Test
    void testCalculateLatency_UnknownType() {
        // Given
        LatencyConfig config = new LatencyConfig();
        setField(config, "type", "UNKNOWN");

        // When
        long latency = (Long) invokePrivateMethod(loadTestService, "calculateLatency", config);

        // Then
        assertEquals(0L, latency);
    }
} 