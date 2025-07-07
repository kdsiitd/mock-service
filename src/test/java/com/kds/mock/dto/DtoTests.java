package com.kds.mock.dto;

import com.kds.mock.entity.Endpoints;
import com.kds.mock.entity.Headers;
import com.kds.mock.entity.Responses;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class DtoTests {

    private final Validator validator;

    public DtoTests() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
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
            if (field == null) throw new NoSuchFieldException("Field " + fieldName + " not found");
            field.setAccessible(true);
            field.set(obj, value);
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
            if (field == null) throw new NoSuchFieldException("Field " + fieldName + " not found");
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testMockEndpointRequestValidation() {
        MockEndpointRequest req = new MockEndpointRequest();
        setField(req, "path", "/api/test");
        setField(req, "method", "GET");
        setField(req, "statusCode", 200);
        setField(req, "contentType", "application/json");
        setField(req, "body", "{\"msg\":\"ok\"}");
        setField(req, "description", "A test endpoint");
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        setField(req, "responseHeaders", headers);
        setField(req, "loadTestConfig", new LoadTestConfig());

        Set<ConstraintViolation<MockEndpointRequest>> violations = validator.validate(req);
        assertTrue(violations.isEmpty(), "No violations for valid request");

        // Test invalid path
        setField(req, "path", "");
        violations = validator.validate(req);
        assertFalse(violations.isEmpty(), "Should have violations for blank path");
        setField(req, "path", "/api/test");

        // Test invalid method
        setField(req, "method", "INVALID");
        violations = validator.validate(req);
        assertFalse(violations.isEmpty(), "Should have violations for invalid method");
        setField(req, "method", "GET");

        // Test invalid status code
        setField(req, "statusCode", 99);
        violations = validator.validate(req);
        assertFalse(violations.isEmpty(), "Should have violations for status code < 100");
        setField(req, "statusCode", 200);

        // Test contentType required when body is present
        setField(req, "contentType", "");
        violations = validator.validate(req);
        assertFalse(violations.isEmpty(), "Should have violations for missing contentType with body");
        setField(req, "contentType", "application/json");

        // Test description too short
        setField(req, "description", "abc");
        violations = validator.validate(req);
        assertFalse(violations.isEmpty(), "Should have violations for short description");
        setField(req, "description", "A test endpoint");
    }

    @Test
    void testMockEndpointResponse() {
        MockEndpointResponse resp = (MockEndpointResponse) createWithNoArgs(MockEndpointResponse.class);
        assertNull(getField(resp, "endpoints"));
        assertNull(getField(resp, "headers"));
        assertNull(getField(resp, "responses"));

        Endpoints ep = (Endpoints) createWithArgs(Endpoints.class, new Class[]{String.class, int.class, String.class}, new Object[]{"/test", 200, "desc"});
        List<Headers> headers = List.of();
        Responses responses = (Responses) createWithArgs(Responses.class, new Class[]{Endpoints.class, String.class, String.class, String.class}, new Object[]{ep, "GET", "application/json", "{}"});
        MockEndpointResponse resp2 = (MockEndpointResponse) createWithArgs(MockEndpointResponse.class, new Class[]{Endpoints.class, List.class, Responses.class}, new Object[]{ep, headers, responses});
        assertEquals(ep, getField(resp2, "endpoints"));
        assertEquals(headers, getField(resp2, "headers"));
        assertEquals(responses, getField(resp2, "responses"));
    }

    @Test
    void testMockResponse() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Test", "value");
        MockResponse resp = (MockResponse) createWithArgs(MockResponse.class, new Class[]{int.class, HttpHeaders.class, String.class}, new Object[]{200, headers, "body"});
        assertEquals(200, getField(resp, "statusCode"));
        assertEquals("body", getField(resp, "body"));
        assertEquals("value", ((HttpHeaders) getField(resp, "headers")).getFirst("X-Test"));
    }

    @Test
    void testErrorResponse() {
        ErrorResponse err1 = (ErrorResponse) createWithArgs(ErrorResponse.class, new Class[]{String.class, String.class}, new Object[]{"msg", "details"});
        assertEquals("msg", getField(err1, "error"));
        assertEquals("details", getField(err1, "message"));
        assertNull(getField(err1, "details"));
        assertEquals(0, getField(err1, "status"));

        List<String> details = Arrays.asList("e1", "e2");
        ErrorResponse err2 = (ErrorResponse) createWithArgs(ErrorResponse.class, new Class[]{String.class, String.class, List.class, int.class}, new Object[]{"msg2", "details2", details, 400});
        assertEquals("msg2", getField(err2, "error"));
        assertEquals("details2", getField(err2, "message"));
        assertEquals(details, getField(err2, "details"));
        assertEquals(400, getField(err2, "status"));
    }

    @Test
    void testLoadTestConfigAndInnerClasses() {
        LoadTestConfig config = new LoadTestConfig();
        LoadTestConfig.LatencyConfig latency = new LoadTestConfig.LatencyConfig();
        setField(latency, "type", "FIXED");
        setField(latency, "fixedLatencyMs", 100L);
        setField(latency, "p50", 50L);
        setField(latency, "p95", 95L);
        setField(latency, "p99", 99L);
        setField(latency, "p999", 999L);
        setField(latency, "minLatencyMs", 10L);
        setField(latency, "maxLatencyMs", 200L);
        setField(latency, "meanLatencyMs", 50.0);
        setField(latency, "stdDeviationMs", 5.0);
        setField(config, "latencyConfig", latency);

        LoadTestConfig.TimeoutConfig timeout = new LoadTestConfig.TimeoutConfig();
        setField(timeout, "enabled", true);
        setField(timeout, "timeoutMs", 1000L);
        setField(timeout, "timeoutProbability", 0.5);
        setField(config, "timeoutConfig", timeout);

        LoadTestConfig.ErrorConfig error = new LoadTestConfig.ErrorConfig();
        setField(error, "enabled", true);
        setField(error, "errorRate", 0.1);
        setField(error, "errorStatusCodes", new Integer[]{500, 502});
        Map<Integer, String> errorMsgs = new HashMap<>();
        errorMsgs.put(500, "Internal Error");
        setField(error, "errorMessages", errorMsgs);
        setField(config, "errorConfig", error);

        LoadTestConfig.RateLimitConfig rateLimit = new LoadTestConfig.RateLimitConfig();
        setField(rateLimit, "enabled", true);
        setField(rateLimit, "requestsPerSecond", 10);
        setField(rateLimit, "burstSize", 5);
        setField(rateLimit, "rateLimitStatusCode", 429);
        setField(config, "rateLimitConfig", rateLimit);

        // Assert all getters via reflection
        assertEquals(latency, getField(config, "latencyConfig"));
        assertEquals(timeout, getField(config, "timeoutConfig"));
        assertEquals(error, getField(config, "errorConfig"));
        assertEquals(rateLimit, getField(config, "rateLimitConfig"));
        assertEquals("FIXED", getField(latency, "type"));
        assertEquals(100L, getField(latency, "fixedLatencyMs"));
        assertEquals(50L, getField(latency, "p50"));
        assertEquals(95L, getField(latency, "p95"));
        assertEquals(99L, getField(latency, "p99"));
        assertEquals(999L, getField(latency, "p999"));
        assertEquals(10L, getField(latency, "minLatencyMs"));
        assertEquals(200L, getField(latency, "maxLatencyMs"));
        assertEquals(50.0, getField(latency, "meanLatencyMs"));
        assertEquals(5.0, getField(latency, "stdDeviationMs"));
        assertEquals(true, getField(timeout, "enabled"));
        assertEquals(1000L, getField(timeout, "timeoutMs"));
        assertEquals(0.5, getField(timeout, "timeoutProbability"));
        assertEquals(true, getField(error, "enabled"));
        assertEquals(0.1, getField(error, "errorRate"));
        assertArrayEquals(new Integer[]{500, 502}, (Integer[]) getField(error, "errorStatusCodes"));
        assertEquals(errorMsgs, getField(error, "errorMessages"));
        assertEquals(true, getField(rateLimit, "enabled"));
        assertEquals(10, getField(rateLimit, "requestsPerSecond"));
        assertEquals(5, getField(rateLimit, "burstSize"));
        assertEquals(429, getField(rateLimit, "rateLimitStatusCode"));
    }

    private Object createWithNoArgs(Class<?> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private Object createWithArgs(Class<?> clazz, Class<?>[] argTypes, Object[] args) {
        try {
            return clazz.getDeclaredConstructor(argTypes).newInstance(args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
} 