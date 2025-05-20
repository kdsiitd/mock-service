package com.kds.mock.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kds.mock.dto.MockEndpointRequest;
import com.kds.mock.dto.MockEndpointResponse;
import com.kds.mock.entity.Endpoints;
import com.kds.mock.entity.Headers;
import com.kds.mock.entity.Responses;
import com.kds.mock.service.MockEndpointConfigureService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MockConfigureController.class)
class MockConfigureControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MockEndpointConfigureService mockEndpointConfigureService;
    
    @BeforeEach
    void setup() {
        // Configure ObjectMapper for tests
        objectMapper.findAndRegisterModules();
    }

    @Test
    void testConfigureMockEndpoint() throws Exception {
        MockEndpointRequest request = getMockEndpointRequest();

        Endpoints endpoint = new Endpoints("/test/path", 200, "Test endpoint");
        endpoint.setId(1L);

        List<Headers> headerList = new ArrayList<>();
        headerList.add(new Headers(endpoint, "Content-Type", MediaType.APPLICATION_JSON_VALUE));
        headerList.add(new Headers(endpoint, "Custom-Header", "custom-value"));

        Responses response = new Responses(endpoint, "GET",
            MediaType.APPLICATION_JSON_VALUE, "{\"name\": \"test\"}");

        MockEndpointResponse mockResponse = new MockEndpointResponse();
        mockResponse.setEndpoints(endpoint);
        mockResponse.setHeaders(headerList);
        mockResponse.setResponses(response);

        when(mockEndpointConfigureService.saveMockEndpoint(any(MockEndpointRequest.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(post("/endpoints/configure")
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
        request.setPath("/test/path");
        request.setMethod("GET");
        request.setStatusCode(200);
        request.setContentType(MediaType.APPLICATION_JSON_VALUE);
        request.setBody("{\"name\": \"test\"}");
        request.setDescription("Test endpoint");

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        headers.put("Custom-Header", "custom-value");
        request.setResponseHeaders(headers);
        return request;
    }

    @Test
    void testConfigureMockEndpointWithError() throws Exception {
        MockEndpointRequest request = new MockEndpointRequest();
        request.setPath("/test/path");
        request.setMethod("GET");
        request.setStatusCode(200);

        when(mockEndpointConfigureService.saveMockEndpoint(any(MockEndpointRequest.class)))
                .thenReturn(null);

        mockMvc.perform(post("/endpoints/configure")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testListMockEndpoints() throws Exception {
        mockMvc.perform(get("/endpoints/list"))
                .andExpect(status().isOk());
    }
} 