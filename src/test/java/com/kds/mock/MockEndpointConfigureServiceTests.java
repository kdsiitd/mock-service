package com.kds.mock;

import com.kds.mock.dto.MockEndpointRequest;
import com.kds.mock.dto.MockEndpointResponse;
import com.kds.mock.entity.Endpoints;
import com.kds.mock.entity.Headers;
import com.kds.mock.entity.Responses;
import com.kds.mock.repository.EndpointsRepository;
import com.kds.mock.repository.HeadersRepository;
import com.kds.mock.repository.ResponsesRepository;
import com.kds.mock.service.MockEndpointConfigureService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class MockEndpointConfigureServiceTests {

    @Autowired
    private MockEndpointConfigureService mockEndpointConfigureService;

    @MockBean
    private EndpointsRepository endpointsRepository;

    @MockBean
    private HeadersRepository headersRepository;

    @MockBean
    private ResponsesRepository responsesRepository;

    private MockEndpointRequest mockRequest;
    private Endpoints savedEndpoint;
    private Headers savedHeader;
    private Responses savedResponse;

    @BeforeEach
    void setUp() {
        mockRequest = new MockEndpointRequest();
        mockRequest.setPath("/test/path");
        mockRequest.setMethod("GET");
        mockRequest.setStatusCode(200);
        mockRequest.setContentType(MediaType.APPLICATION_JSON_VALUE);
        mockRequest.setBody("{\"name\": \"test\"}");
        mockRequest.setDescription("Test endpoint");

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        headers.put("Custom-Header", "custom-value");
        mockRequest.setResponseHeaders(headers);

        savedEndpoint = new Endpoints("/test/path", 200, "Test endpoint");
        savedEndpoint.setId(1L);

        savedHeader = new Headers(savedEndpoint, "Content-Type", MediaType.APPLICATION_JSON_VALUE);
        savedHeader.setId(1L);

        savedResponse = new Responses(savedEndpoint, "GET", 
            MediaType.APPLICATION_JSON_VALUE, "{\"name\": \"test\"}");
        savedResponse.setId(1L);
    }

    @Test
    void testSaveMockEndpoint() {
        when(endpointsRepository.save(any(Endpoints.class))).thenReturn(savedEndpoint);
        when(headersRepository.save(any(Headers.class))).thenReturn(savedHeader);
        when(responsesRepository.save(any(Responses.class))).thenReturn(savedResponse);

        MockEndpointResponse response = mockEndpointConfigureService.saveMockEndpoint(mockRequest);

        assertNotNull(response);
        assertAll(
            () -> assertEquals("/test/path", response.getEndpoints().getPath()),
            () -> assertEquals(200, response.getEndpoints().getStatusCode()),
            () -> assertEquals("Test endpoint", response.getEndpoints().getDescription()),
            () -> assertNotNull(response.getHeaders()),
            () -> assertFalse(response.getHeaders().isEmpty()),
            () -> assertNotNull(response.getResponses()),
            () -> assertEquals("GET", response.getResponses().getMethod()),
            () -> assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getResponses().getContentType()),
            () -> assertEquals("{\"name\": \"test\"}", response.getResponses().getBody())
        );

        verify(endpointsRepository, times(1)).save(any(Endpoints.class));
        verify(headersRepository, times(2)).save(any(Headers.class));
        verify(responsesRepository, times(1)).save(any(Responses.class));
    }

    @Test
    void testSaveMockEndpointWithError() {
        when(endpointsRepository.save(any(Endpoints.class))).thenThrow(new RuntimeException("Database error"));

        MockEndpointResponse response = mockEndpointConfigureService.saveMockEndpoint(mockRequest);

        assertNull(response);
        verify(endpointsRepository, times(1)).save(any(Endpoints.class));
        verify(headersRepository, never()).save(any(Headers.class));
        verify(responsesRepository, never()).save(any(Responses.class));
    }

    @Test
    void testSaveMockEndpointWithNoHeaders() {
        mockRequest.setResponseHeaders(new HashMap<>());
        when(endpointsRepository.save(any(Endpoints.class))).thenReturn(savedEndpoint);
        when(responsesRepository.save(any(Responses.class))).thenReturn(savedResponse);

        MockEndpointResponse response = mockEndpointConfigureService.saveMockEndpoint(mockRequest);

        assertNotNull(response);
        assertAll(
            () -> assertEquals("/test/path", response.getEndpoints().getPath()),
            () -> assertNotNull(response.getHeaders()),
            () -> assertTrue(response.getHeaders().isEmpty()),
            () -> assertNotNull(response.getResponses())
        );

        verify(endpointsRepository, times(1)).save(any(Endpoints.class));
        verify(headersRepository, never()).save(any(Headers.class));
        verify(responsesRepository, times(1)).save(any(Responses.class));
    }
}