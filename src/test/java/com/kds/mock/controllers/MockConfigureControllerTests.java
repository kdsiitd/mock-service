package com.kds.mock.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kds.mock.dto.MockEndpointRequest;
import com.kds.mock.dto.MockEndpointResponse;
import com.kds.mock.dto.UpdateMockEndpointRequest;
import com.kds.mock.dto.LoadTestConfig;
import com.kds.mock.entity.Endpoints;
import com.kds.mock.entity.Headers;
import com.kds.mock.entity.Responses;
import com.kds.mock.service.MockEndpointConfigureService;
import com.kds.mock.service.LoadTestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MockConfigureController.class)
class MockConfigureControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MockEndpointConfigureService mockEndpointConfigureService;
    
    @MockBean
    private LoadTestService loadTestService;
    
    @BeforeEach
    void setup() {
        // Configure ObjectMapper for tests
        objectMapper.findAndRegisterModules();
    }

    @Test
    void testConfigureMockEndpoint() throws Exception {
        MockEndpointRequest request = getMockEndpointRequest();

        Endpoints endpoint = new Endpoints("/test/path", 200, "Test endpoint");
        
        List<Headers> headerList = new ArrayList<>();
        headerList.add(new Headers(endpoint, "Content-Type", MediaType.APPLICATION_JSON_VALUE));
        headerList.add(new Headers(endpoint, "Custom-Header", "custom-value"));

        Responses response = new Responses(endpoint, "GET",
            MediaType.APPLICATION_JSON_VALUE, "{\"name\": \"test\"}");

        MockEndpointResponse mockResponse = new MockEndpointResponse(endpoint, headerList, response);

        when(mockEndpointConfigureService.saveMockEndpoint(any(MockEndpointRequest.class)))
                .thenReturn(mockResponse);
        
        when(loadTestService.toJson(any())).thenReturn("{}");

        mockMvc.perform(post("/endpoints")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.endpoints.path").value("/test/path"))
                .andExpect(jsonPath("$.endpoints.statusCode").value(200))
                .andExpect(jsonPath("$.endpoints.description").value("Test endpoint"))
                .andExpect(jsonPath("$.headers").isArray())
                .andExpect(jsonPath("$.responses.method").value("GET"))
                .andExpect(jsonPath("$.responses.contentType").value(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.responses.body").value("{\"name\": \"test\"}"));
    }

    private MockEndpointRequest getMockEndpointRequest() {
        MockEndpointRequest request = new MockEndpointRequest();
        setField(request, "path", "/test/path");
        setField(request, "method", "GET");
        setField(request, "statusCode", 200);
        setField(request, "contentType", MediaType.APPLICATION_JSON_VALUE);
        setField(request, "body", "{\"name\": \"test\"}");
        setField(request, "description", "Test endpoint");

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        headers.put("Custom-Header", "custom-value");
        setField(request, "responseHeaders", headers);
        return request;
    }

    @Test
    void testConfigureMockEndpointWithError() throws Exception {
        MockEndpointRequest request = new MockEndpointRequest();
        setField(request, "path", "/test/path");
        setField(request, "method", "GET");
        setField(request, "statusCode", 200);
        setField(request, "description", "Test endpoint");

        when(mockEndpointConfigureService.saveMockEndpoint(any(MockEndpointRequest.class)))
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(post("/endpoints")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testListMockEndpoints() throws Exception {
        Endpoints endpoint = new Endpoints("/test/path", 200, "Test endpoint");
        List<Headers> headerList = new ArrayList<>();
        Responses response = new Responses(endpoint, "GET", MediaType.APPLICATION_JSON_VALUE, "{\"name\": \"test\"}");
        MockEndpointResponse mockResponse = new MockEndpointResponse(endpoint, headerList, response);

        List<MockEndpointResponse> responseList = new ArrayList<>();
        responseList.add(mockResponse);
        
        when(mockEndpointConfigureService.getAllMockEndpoints()).thenReturn(responseList);

        mockMvc.perform(get("/endpoints"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].endpoints.path").value("/test/path"));
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

    @Test
    void testUpdateMockEndpoint_Success() throws Exception {
        // Given
        String path = "/api/users";
        String method = "GET";
        UpdateMockEndpointRequest updateRequest = new UpdateMockEndpointRequest();
        setField(updateRequest, "statusCode", 200);
        setField(updateRequest, "body", "{\"users\": [{\"id\": 1, \"name\": \"Updated User\"}]}");
        setField(updateRequest, "contentType", MediaType.APPLICATION_JSON_VALUE);
        setField(updateRequest, "description", "Updated mock users endpoint");

        Endpoints updatedEndpoint = new Endpoints(path, 200, "Updated mock users endpoint");
        List<Headers> headerList = new ArrayList<>();
        Responses updatedResponse = new Responses(updatedEndpoint, method, MediaType.APPLICATION_JSON_VALUE, "{\"users\": [{\"id\": 1, \"name\": \"Updated User\"}]}");
        MockEndpointResponse mockResponse = new MockEndpointResponse(updatedEndpoint, headerList, updatedResponse);

        when(mockEndpointConfigureService.updateMockEndpoint(eq(path), eq(method), any(UpdateMockEndpointRequest.class)))
                .thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(put("/endpoints/" + java.net.URLEncoder.encode(path, "UTF-8"))
                .param("method", method)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.endpoints.path").value(path))
                .andExpect(jsonPath("$.endpoints.description").value("Updated mock users endpoint"))
                .andExpect(jsonPath("$.responses.body").value("{\"users\": [{\"id\": 1, \"name\": \"Updated User\"}]}"));
    }

    @Test
    void testUpdateMockEndpoint_NotFound() throws Exception {
        // Given
        String path = "/api/nonexistent";
        String method = "GET";
        UpdateMockEndpointRequest updateRequest = new UpdateMockEndpointRequest();
        setField(updateRequest, "statusCode", 200);

        when(mockEndpointConfigureService.updateMockEndpoint(eq(path), eq(method), any(UpdateMockEndpointRequest.class)))
                .thenThrow(new IllegalArgumentException("Mock endpoint not found for path: " + path + " and method: " + method));

        // When & Then
        mockMvc.perform(put("/endpoints/" + java.net.URLEncoder.encode(path, "UTF-8"))
                .param("method", method)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateMockEndpoint_WithLoadTestConfig() throws Exception {
        // Given
        String path = "/api/load-test";
        String method = "GET";
        UpdateMockEndpointRequest updateRequest = new UpdateMockEndpointRequest();
        setField(updateRequest, "loadTestConfig", createLoadTestConfig());

        Endpoints updatedEndpoint = new Endpoints(path, 200, "Load test endpoint");
        List<Headers> headerList = new ArrayList<>();
        Responses updatedResponse = new Responses(updatedEndpoint, method, MediaType.APPLICATION_JSON_VALUE, "{\"message\": \"Load test\"}");
        MockEndpointResponse mockResponse = new MockEndpointResponse(updatedEndpoint, headerList, updatedResponse);

        when(mockEndpointConfigureService.updateMockEndpoint(eq(path), eq(method), any(UpdateMockEndpointRequest.class)))
                .thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(put("/endpoints/" + java.net.URLEncoder.encode(path, "UTF-8"))
                .param("method", method)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.endpoints.path").value(path));
    }

    @Test
    void testDeleteMockEndpoint_Success() throws Exception {
        // Given
        String path = "/api/users";
        String method = "GET";

        // When & Then
        mockMvc.perform(delete("/endpoints/" + java.net.URLEncoder.encode(path, "UTF-8"))
                .param("method", method))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Mock endpoint deleted successfully"))
                .andExpect(jsonPath("$.deletedPath").value(path))
                .andExpect(jsonPath("$.deletedMethod").value(method));
    }

    @Test
    void testDeleteMockEndpoint_NotFound() throws Exception {
        // Given
        String path = "/api/nonexistent";
        String method = "GET";

        doThrow(new IllegalArgumentException("Mock endpoint not found for path: " + path + " and method: " + method))
                .when(mockEndpointConfigureService).deleteMockEndpoint(path, method);

        // When & Then
        mockMvc.perform(delete("/endpoints/" + java.net.URLEncoder.encode(path, "UTF-8"))
                .param("method", method))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetMockEndpoint_Success() throws Exception {
        // Given
        String path = "/api/users";
        String method = "GET";

        Endpoints endpoint = new Endpoints(path, 200, "Mock users endpoint");
        List<Headers> headerList = new ArrayList<>();
        headerList.add(new Headers(endpoint, "Content-Type", MediaType.APPLICATION_JSON_VALUE));
        Responses response = new Responses(endpoint, method, MediaType.APPLICATION_JSON_VALUE, "{\"users\": []}");
        MockEndpointResponse mockResponse = new MockEndpointResponse(endpoint, headerList, response);

        when(mockEndpointConfigureService.getMockEndpoint(path, method))
                .thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(get("/endpoints/" + java.net.URLEncoder.encode(path, "UTF-8"))
                .param("method", method))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.endpoints.path").value(path))
                .andExpect(jsonPath("$.endpoints.description").value("Mock users endpoint"))
                .andExpect(jsonPath("$.responses.method").value(method))
                .andExpect(jsonPath("$.headers").isArray());
    }

    @Test
    void testGetMockEndpoint_NotFound() throws Exception {
        // Given
        String path = "/api/nonexistent";
        String method = "GET";

        when(mockEndpointConfigureService.getMockEndpoint(path, method))
                .thenThrow(new IllegalArgumentException("Mock endpoint not found for path: " + path + " and method: " + method));

        // When & Then
        mockMvc.perform(get("/endpoints/" + java.net.URLEncoder.encode(path, "UTF-8"))
                .param("method", method))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateMockEndpoint_ValidationError() throws Exception {
        // Given
        String path = "/api/users";
        String method = "GET";
        UpdateMockEndpointRequest updateRequest = new UpdateMockEndpointRequest();
        setField(updateRequest, "statusCode", 999); // Invalid status code

        // When & Then
        mockMvc.perform(put("/endpoints/" + java.net.URLEncoder.encode(path, "UTF-8"))
                .param("method", method)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateMockEndpoint_WithHeaders() throws Exception {
        // Given
        String path = "/api/users";
        String method = "GET";
        UpdateMockEndpointRequest updateRequest = new UpdateMockEndpointRequest();
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Cache-Control", "max-age=3600");
        setField(updateRequest, "responseHeaders", headers);

        Endpoints updatedEndpoint = new Endpoints(path, 200, "Mock users endpoint");
        List<Headers> headerList = new ArrayList<>();
        headerList.add(new Headers(updatedEndpoint, "Content-Type", "application/json"));
        headerList.add(new Headers(updatedEndpoint, "Cache-Control", "max-age=3600"));
        Responses updatedResponse = new Responses(updatedEndpoint, method, MediaType.APPLICATION_JSON_VALUE, "{\"users\": []}");
        MockEndpointResponse mockResponse = new MockEndpointResponse(updatedEndpoint, headerList, updatedResponse);

        when(mockEndpointConfigureService.updateMockEndpoint(eq(path), eq(method), any(UpdateMockEndpointRequest.class)))
                .thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(put("/endpoints/" + java.net.URLEncoder.encode(path, "UTF-8"))
                .param("method", method)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.headers").isArray())
                .andExpect(jsonPath("$.headers.length()").value(2));
    }

    @Test
    void testCRUDOperations_WithSpecialCharactersInPath() throws Exception {
        // Given
        String path = "/api/users/123?filter=active";
        String method = "GET";
        MockEndpointRequest request = new MockEndpointRequest();
        setField(request, "statusCode", 200);

        Endpoints endpoint = new Endpoints(path, 200, "Test endpoint");
        List<Headers> headerList = new ArrayList<>();
        Responses response = new Responses(endpoint, method, MediaType.APPLICATION_JSON_VALUE, "{\"user\": {\"id\": 123}}");
        MockEndpointResponse mockResponse = new MockEndpointResponse(endpoint, headerList, response);

        when(mockEndpointConfigureService.getMockEndpoint(path, method))
                .thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(get("/endpoints/" + java.net.URLEncoder.encode(path, "UTF-8"))
                .param("method", method))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.endpoints.path").value(path));
    }

    private LoadTestConfig createLoadTestConfig() {
        LoadTestConfig config = new LoadTestConfig();
        
        LoadTestConfig.LatencyConfig latencyConfig = new LoadTestConfig.LatencyConfig();
        setField(latencyConfig, "type", "PERCENTILE");
        setField(latencyConfig, "p50", 100L);
        setField(latencyConfig, "p95", 500L);
        setField(latencyConfig, "p99", 1000L);
        setField(config, "latencyConfig", latencyConfig);
        
        LoadTestConfig.TimeoutConfig timeoutConfig = new LoadTestConfig.TimeoutConfig();
        setField(timeoutConfig, "enabled", true);
        setField(timeoutConfig, "timeoutMs", 5000L);
        setField(timeoutConfig, "timeoutProbability", 0.1);
        setField(config, "timeoutConfig", timeoutConfig);
        
        LoadTestConfig.ErrorConfig errorConfig = new LoadTestConfig.ErrorConfig();
        setField(errorConfig, "enabled", true);
        setField(errorConfig, "errorRate", 0.05);
        setField(config, "errorConfig", errorConfig);
        
        LoadTestConfig.RateLimitConfig rateLimitConfig = new LoadTestConfig.RateLimitConfig();
        setField(rateLimitConfig, "enabled", true);
        setField(rateLimitConfig, "requestsPerSecond", 100);
        setField(rateLimitConfig, "burstSize", 10);
        setField(rateLimitConfig, "rateLimitStatusCode", 429);
        setField(config, "rateLimitConfig", rateLimitConfig);
        
        return config;
    }

    @Test
    void testGetEndpointById_Success() throws Exception {
        // Given
        Long endpointId = 1L;

        Endpoints endpoint = new Endpoints("/api/users", 200, "Mock users endpoint");
        List<Headers> headerList = new ArrayList<>();
        headerList.add(new Headers(endpoint, "Content-Type", MediaType.APPLICATION_JSON_VALUE));
        Responses response = new Responses(endpoint, "GET", MediaType.APPLICATION_JSON_VALUE, "{\"users\": []}");
        MockEndpointResponse mockResponse = new MockEndpointResponse(endpoint, headerList, response);

        when(mockEndpointConfigureService.getMockEndpointById(endpointId))
                .thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(get("/endpoints/id/" + endpointId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.endpoints.path").value("/api/users"))
                .andExpect(jsonPath("$.endpoints.description").value("Mock users endpoint"))
                .andExpect(jsonPath("$.responses.method").value("GET"))
                .andExpect(jsonPath("$.headers").isArray());
    }

    @Test
    void testGetEndpointById_NotFound() throws Exception {
        // Given
        Long endpointId = 999L;

        when(mockEndpointConfigureService.getMockEndpointById(endpointId))
                .thenThrow(new IllegalArgumentException("Mock endpoint not found with ID: " + endpointId));

        // When & Then
        mockMvc.perform(get("/endpoints/id/" + endpointId))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testConfigureMockEndpoint_WithLoadTestConfig_ReturnsDeserializedObject() throws Exception {
        // Given
        MockEndpointRequest request = getMockEndpointRequest();
        request.setLoadTestConfig(createLoadTestConfig());

        Endpoints endpoint = new Endpoints("/test/path", 200, "Test endpoint");
        endpoint.setLoadTestConfig("{\"latencyConfig\":{\"type\":\"PERCENTILE\",\"p50\":100,\"p95\":500,\"p99\":1000}}");
        
        List<Headers> headerList = new ArrayList<>();
        headerList.add(new Headers(endpoint, "Content-Type", MediaType.APPLICATION_JSON_VALUE));

        Responses response = new Responses(endpoint, "GET",
            MediaType.APPLICATION_JSON_VALUE, "{\"name\": \"test\"}");

        MockEndpointResponse mockResponse = new MockEndpointResponse(endpoint, headerList, response, createLoadTestConfig());

        when(mockEndpointConfigureService.saveMockEndpoint(any(MockEndpointRequest.class)))
                .thenReturn(mockResponse);
        
        when(loadTestService.toJson(any())).thenReturn("{}");

        // When & Then
        mockMvc.perform(post("/endpoints")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.endpoints.path").value("/test/path"))
                .andExpect(jsonPath("$.endpoints.statusCode").value(200))
                .andExpect(jsonPath("$.endpoints.description").value("Test endpoint"))
                .andExpect(jsonPath("$.endpoints.loadTestConfig").doesNotExist()) // Should not exist in endpoints object
                .andExpect(jsonPath("$.loadTestConfig").exists()) // Should exist as top-level object
                .andExpect(jsonPath("$.loadTestConfig.latencyConfig.type").value("PERCENTILE"))
                .andExpect(jsonPath("$.loadTestConfig.latencyConfig.p50").value(100))
                .andExpect(jsonPath("$.loadTestConfig.latencyConfig.p95").value(500))
                .andExpect(jsonPath("$.loadTestConfig.latencyConfig.p99").value(1000));
    }

    @Test
    void testUpdateMockEndpoint_WithLoadTestConfig_ReturnsDeserializedObject() throws Exception {
        // Given
        String path = "/api/v1/inventory";
        String method = "GET";
        UpdateMockEndpointRequest updateRequest = new UpdateMockEndpointRequest();
        setField(updateRequest, "statusCode", 200);
        setField(updateRequest, "description", "Mock inventory endpoint for testing with timeout");
        setField(updateRequest, "loadTestConfig", createLoadTestConfig());

        Endpoints updatedEndpoint = new Endpoints(path, 200, "Mock inventory endpoint for testing with timeout");
        updatedEndpoint.setLoadTestConfig("{\"latencyConfig\":{\"type\":\"PERCENTILE\",\"p50\":100,\"p95\":500,\"p99\":1000}}");
        
        List<Headers> headerList = new ArrayList<>();
        headerList.add(new Headers(updatedEndpoint, "Content-Type", MediaType.APPLICATION_JSON_VALUE));
        Responses updatedResponse = new Responses(updatedEndpoint, method, MediaType.APPLICATION_JSON_VALUE, "{\"inventory\": []}");
        MockEndpointResponse mockResponse = new MockEndpointResponse(updatedEndpoint, headerList, updatedResponse, createLoadTestConfig());

        when(mockEndpointConfigureService.updateMockEndpoint(eq(path), eq(method), any(UpdateMockEndpointRequest.class)))
                .thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(put("/endpoints/" + java.net.URLEncoder.encode(path, "UTF-8"))
                .param("method", method)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.endpoints.path").value(path))
                .andExpect(jsonPath("$.endpoints.description").value("Mock inventory endpoint for testing with timeout"))
                .andExpect(jsonPath("$.endpoints.loadTestConfig").doesNotExist()) // Should not exist in endpoints object
                .andExpect(jsonPath("$.loadTestConfig").exists()) // Should exist as top-level object
                .andExpect(jsonPath("$.loadTestConfig.latencyConfig.type").value("PERCENTILE"))
                .andExpect(jsonPath("$.loadTestConfig.latencyConfig.p50").value(100))
                .andExpect(jsonPath("$.loadTestConfig.latencyConfig.p95").value(500))
                .andExpect(jsonPath("$.loadTestConfig.latencyConfig.p99").value(1000))
                .andExpect(jsonPath("$.loadTestConfig.timeoutConfig.enabled").value(true))
                .andExpect(jsonPath("$.loadTestConfig.timeoutConfig.timeoutProbability").value(0.1))
                .andExpect(jsonPath("$.loadTestConfig.errorConfig.enabled").value(true))
                .andExpect(jsonPath("$.loadTestConfig.errorConfig.errorRate").value(0.05));
    }
} 