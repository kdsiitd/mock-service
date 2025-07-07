package com.kds.mock.service.impl;

import com.kds.mock.dto.MockEndpointRequest;
import com.kds.mock.dto.MockEndpointResponse;
import com.kds.mock.dto.LoadTestConfig;
import com.kds.mock.entity.Endpoints;
import com.kds.mock.entity.Headers;
import com.kds.mock.entity.Responses;
import com.kds.mock.repository.EndpointsRepository;
import com.kds.mock.repository.HeadersRepository;
import com.kds.mock.repository.ResponsesRepository;
import com.kds.mock.service.LoadTestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class MockEndpointConfigureServiceImplTests {

    @Mock
    private EndpointsRepository endpointsRepository;
    @Mock
    private HeadersRepository headersRepository;
    @Mock
    private ResponsesRepository responsesRepository;
    @Mock
    private LoadTestService loadTestService;

    private MockEndpointConfigureServiceImpl mockEndpointConfigureService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        try {
            java.lang.reflect.Constructor<MockEndpointConfigureServiceImpl> constructor = 
                MockEndpointConfigureServiceImpl.class.getDeclaredConstructor(
                    EndpointsRepository.class, HeadersRepository.class, 
                    ResponsesRepository.class, LoadTestService.class);
            constructor.setAccessible(true);
            mockEndpointConfigureService = constructor.newInstance(
                endpointsRepository, headersRepository, responsesRepository, loadTestService);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setField(Object obj, String fieldName, Object value) {
        try {
            Class<?> clazz = obj.getClass();
            Field field = null;
            
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

    @Test
    void testSaveMockEndpoint_Success() {
        // Given
        MockEndpointRequest request = createMockEndpointRequest();
        Endpoints savedEndpoint = new Endpoints("/test", 200, "desc");
        Headers savedHeader = new Headers(savedEndpoint, "Content-Type", "application/json");
        Responses savedResponse = new Responses(savedEndpoint, "GET", "application/json", "{}");
        
        when(loadTestService.toJson(any(LoadTestConfig.class))).thenReturn("{}");
        when(endpointsRepository.save(any(Endpoints.class))).thenReturn(savedEndpoint);
        when(headersRepository.save(any(Headers.class))).thenReturn(savedHeader);
        when(responsesRepository.save(any(Responses.class))).thenReturn(savedResponse);

        // When
        MockEndpointResponse response = mockEndpointConfigureService.saveMockEndpoint(request);

        // Then
        assertNotNull(response);
        verify(endpointsRepository).save(any(Endpoints.class));
        verify(headersRepository, times(2)).save(any(Headers.class));
        verify(responsesRepository).save(any(Responses.class));
    }

    @Test
    void testSaveMockEndpoint_WithNullLoadTestConfig() {
        // Given
        MockEndpointRequest request = createMockEndpointRequest();
        setField(request, "loadTestConfig", null);

        // When
        MockEndpointResponse response = mockEndpointConfigureService.saveMockEndpoint(request);

        // Then
        assertNotNull(response);
        verify(endpointsRepository).save(any(Endpoints.class));
    }

    @Test
    void testSaveMockEndpoint_WithNullHeaders() {
        // Given
        MockEndpointRequest request = createMockEndpointRequest();
        setField(request, "responseHeaders", null);

        // When
        MockEndpointResponse response = mockEndpointConfigureService.saveMockEndpoint(request);

        // Then
        assertNotNull(response);
        verify(endpointsRepository).save(any(Endpoints.class));
        verify(headersRepository, never()).save(any(Headers.class));
    }

    @Test
    void testSaveMockEndpoint_SerializationError() {
        // Given
        MockEndpointRequest request = createMockEndpointRequest();
        when(loadTestService.toJson(any(LoadTestConfig.class))).thenThrow(new RuntimeException("Serialization error"));

        // When & Then
        assertThrows(RuntimeException.class, () -> mockEndpointConfigureService.saveMockEndpoint(request));
        verify(endpointsRepository, never()).save(any(Endpoints.class));
    }

    @Test
    void testSaveMockEndpoint_RepositoryError() {
        // Given
        MockEndpointRequest request = createMockEndpointRequest();
        when(endpointsRepository.save(any(Endpoints.class))).thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThrows(RuntimeException.class, () -> mockEndpointConfigureService.saveMockEndpoint(request));
    }

    @Test
    void testGetAllMockEndpoints_Success() {
        // Given
        List<Endpoints> endpoints = Arrays.asList(
            new Endpoints("/test1", 200, "desc1"),
            new Endpoints("/test2", 404, "desc2")
        );
        when(endpointsRepository.findAll()).thenReturn(endpoints);
        when(headersRepository.findAllByEndpointsId(anyLong())).thenReturn(new ArrayList<>());
        when(responsesRepository.findAll()).thenReturn(new ArrayList<>());

        // When
        List<MockEndpointResponse> responses = mockEndpointConfigureService.getAllMockEndpoints();

        // Then
        assertNotNull(responses);
        assertEquals(2, responses.size());
        verify(endpointsRepository).findAll();
    }

    @Test
    void testGetAllMockEndpoints_WithResponses() {
        // Given
        Endpoints endpoint = new Endpoints("/test", 200, "desc");
        setField(endpoint, "id", 1L);
        
        Responses response = new Responses(endpoint, "GET", "application/json", "{}");
        setField(response, "id", 1L);
        
        List<Endpoints> endpoints = Arrays.asList(endpoint);
        List<Responses> allResponses = Arrays.asList(response);
        
        when(endpointsRepository.findAll()).thenReturn(endpoints);
        when(headersRepository.findAllByEndpointsId(anyLong())).thenReturn(new ArrayList<>());
        when(responsesRepository.findAll()).thenReturn(allResponses);

        // When
        List<MockEndpointResponse> responses = mockEndpointConfigureService.getAllMockEndpoints();

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertNotNull(getField(responses.get(0), "responses"));
    }

    @Test
    void testGetAllMockEndpoints_NoResponses() {
        // Given
        Endpoints endpoint = new Endpoints("/test", 200, "desc");
        setField(endpoint, "id", 1L);
        
        List<Endpoints> endpoints = Arrays.asList(endpoint);
        when(endpointsRepository.findAll()).thenReturn(endpoints);
        when(headersRepository.findAllByEndpointsId(anyLong())).thenReturn(new ArrayList<>());
        when(responsesRepository.findAll()).thenReturn(new ArrayList<>());

        // When
        List<MockEndpointResponse> responses = mockEndpointConfigureService.getAllMockEndpoints();

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertNull(getField(responses.get(0), "responses"));
    }

    @Test
    void testGetAllMockEndpoints_EmptyList() {
        // Given
        when(endpointsRepository.findAll()).thenReturn(new ArrayList<>());

        // When
        List<MockEndpointResponse> responses = mockEndpointConfigureService.getAllMockEndpoints();

        // Then
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
    }

    private MockEndpointRequest createMockEndpointRequest() {
        MockEndpointRequest request = new MockEndpointRequest();
        setField(request, "path", "/test");
        setField(request, "method", "GET");
        setField(request, "statusCode", 200);
        setField(request, "contentType", "application/json");
        setField(request, "body", "{\"message\":\"test\"}");
        setField(request, "description", "Test endpoint");
        
        LoadTestConfig loadTestConfig = new LoadTestConfig();
        setField(request, "loadTestConfig", loadTestConfig);
        
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Test", "value");
        setField(request, "responseHeaders", headers);
        
        return request;
    }

    private Object getField(Object obj, String fieldName) {
        try {
            Class<?> clazz = obj.getClass();
            Field field = null;
            
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
} 