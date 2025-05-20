package com.kds.mock.controllers;

import com.kds.mock.dto.MockResponse;
import com.kds.mock.service.MockService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MockRequestController.class)
class MockRequestControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MockService mockService;

    @Test
    void testHandleMockRequestWithValidEndpoint() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Custom-Header", "custom-value");

        MockResponse mockResponse = new MockResponse(200, headers, "{\"name\": \"test\"}");
        when(mockService.getMockResponseByPathAndMethod(anyString(), anyString())).thenReturn(mockResponse);

        mockMvc.perform(get("/api/test/path"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(header().string("Custom-Header", "custom-value"))
                .andExpect(content().json("{\"name\": \"test\"}"));
    }

    @Test
    void testHandleMockRequestWithNonExistentEndpoint() throws Exception {
        when(mockService.getMockResponseByPathAndMethod(anyString(), anyString())).thenReturn(null);

        mockMvc.perform(get("/api/non-existent"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Mock endpoint not configured."));
    }

    @Test
    void testHandleMockRequestWithErrorStatusCode() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        MockResponse mockResponse = new MockResponse(500, headers, "{\"error\": \"Internal Server Error\"}");
        when(mockService.getMockResponseByPathAndMethod(anyString(), anyString())).thenReturn(mockResponse);

        mockMvc.perform(get("/api/error/path"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"error\": \"Internal Server Error\"}"));
    }
} 