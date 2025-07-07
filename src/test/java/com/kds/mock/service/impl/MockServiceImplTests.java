package com.kds.mock.service.impl;

import com.kds.mock.dto.MockResponse;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class MockServiceImplTests {

    @Mock
    private EndpointsRepository endpointsRepository;
    @Mock
    private HeadersRepository headersRepository;
    @Mock
    private ResponsesRepository responsesRepository;
    @Mock
    private LoadTestService loadTestService;

    @InjectMocks
    private MockServiceImpl mockService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        try {
            java.lang.reflect.Constructor<MockServiceImpl> constructor = MockServiceImpl.class.getDeclaredConstructor(EndpointsRepository.class, HeadersRepository.class, ResponsesRepository.class, LoadTestService.class);
            constructor.setAccessible(true);
            mockService = constructor.newInstance(endpointsRepository, headersRepository, responsesRepository, loadTestService);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setField(Object obj, String fieldName, Object value) {
        try {
            Class<?> clazz = obj.getClass();
            Field field = null;
            
            // Search for the field in the class hierarchy
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

    private int getStatusCode(MockResponse response) {
        try {
            Field field = MockResponse.class.getDeclaredField("statusCode");
            field.setAccessible(true);
            return (int) field.get(response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getBody(MockResponse response) {
        try {
            Field field = MockResponse.class.getDeclaredField("body");
            field.setAccessible(true);
            return (String) field.get(response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetMockResponseByPathAndMethod_ReturnsNullIfEndpointNotFound() {
        when(endpointsRepository.findEndpointByPath(anyString())).thenReturn(null);
        MockResponse response = mockService.getMockResponseByPathAndMethod("/notfound", "GET");
        assertNull(response);
    }

    @Test
    void testGetMockResponseByPathAndMethod_RateLimitExceeded() {
        Endpoints endpoint = new Endpoints("/test", 200, "desc");
        setField(endpoint, "loadTestConfig", "{}");
        when(endpointsRepository.findEndpointByPath(anyString())).thenReturn(endpoint);
        when(loadTestService.parseLoadTestConfig(anyString())).thenReturn(new LoadTestConfig());
        when(loadTestService.isRateLimitExceeded(anyString(), any())).thenReturn(true);
        MockResponse response = mockService.getMockResponseByPathAndMethod("/test", "GET");
        assertNotNull(response);
        assertEquals(429, getStatusCode(response));
        assertTrue(getBody(response).contains("Rate limit exceeded"));
    }

    @Test
    void testGetMockResponseByPathAndMethod_Timeout() {
        Endpoints endpoint = new Endpoints("/test", 200, "desc");
        setField(endpoint, "loadTestConfig", "{}");
        when(endpointsRepository.findEndpointByPath(anyString())).thenReturn(endpoint);
        when(loadTestService.parseLoadTestConfig(anyString())).thenReturn(new LoadTestConfig());
        when(loadTestService.isRateLimitExceeded(anyString(), any())).thenReturn(false);
        when(loadTestService.shouldTimeout(any())).thenReturn(true);
        MockResponse response = mockService.getMockResponseByPathAndMethod("/test", "GET");
        assertNotNull(response);
        assertEquals(HttpStatus.REQUEST_TIMEOUT.value(), getStatusCode(response));
        assertTrue(getBody(response).contains("Request timed out"));
    }

    @Test
    void testGetMockResponseByPathAndMethod_ErrorSimulation() {
        Endpoints endpoint = new Endpoints("/test", 200, "desc");
        setField(endpoint, "loadTestConfig", "{}");
        when(endpointsRepository.findEndpointByPath(anyString())).thenReturn(endpoint);
        when(loadTestService.parseLoadTestConfig(anyString())).thenReturn(new LoadTestConfig());
        when(loadTestService.isRateLimitExceeded(anyString(), any())).thenReturn(false);
        when(loadTestService.shouldTimeout(any())).thenReturn(false);
        when(loadTestService.shouldGenerateError(any())).thenReturn(true);
        when(loadTestService.getRandomErrorStatusCode(any())).thenReturn(500);
        when(loadTestService.getErrorMessage(any(), eq(500))).thenReturn("Simulated error");
        MockResponse response = mockService.getMockResponseByPathAndMethod("/test", "GET");
        assertNotNull(response);
        assertEquals(500, getStatusCode(response));
        assertTrue(getBody(response).contains("Simulated error"));
    }

    @Test
    void testGetMockResponseByPathAndMethod_NormalResponse() {
        try {
            Endpoints endpoint = new Endpoints("/test", 200, "desc");
            setField(endpoint, "id", 1L);
            setField(endpoint, "loadTestConfig", "{}");
            when(endpointsRepository.findEndpointByPath(anyString())).thenReturn(endpoint);
            when(loadTestService.parseLoadTestConfig(anyString())).thenReturn(new LoadTestConfig());
            when(loadTestService.isRateLimitExceeded(anyString(), any())).thenReturn(false);
            when(loadTestService.shouldTimeout(any())).thenReturn(false);
            when(loadTestService.shouldGenerateError(any())).thenReturn(false);
            doAnswer(invocation -> null).when(loadTestService).simulateLatency(any());
            List<Headers> headers = Collections.singletonList(new Headers(endpoint, "X-Test", "value"));
            when(headersRepository.findAllByEndpointsId(anyLong())).thenReturn(headers);
            Responses responseEntity = new Responses(endpoint, "GET", "application/json", "{\"result\":\"ok\"}");
            when(responsesRepository.findResponseByEndpointsIdAndMethod(anyLong(), anyString())).thenReturn(responseEntity);
            MockResponse response = mockService.getMockResponseByPathAndMethod("/test", "GET");
            assertNotNull(response);
            assertEquals(200, getStatusCode(response));
            assertTrue(getBody(response).contains("ok"));
        } catch (InterruptedException e) {
            fail("InterruptedException should not be thrown");
        }
    }

    @Test
    void testGetMockResponseByPathAndMethod_ExceptionHandling() {
        Endpoints endpoint = new Endpoints("/test", 200, "desc");
        setField(endpoint, "loadTestConfig", "{}");
        when(endpointsRepository.findEndpointByPath(anyString())).thenReturn(endpoint);
        when(loadTestService.parseLoadTestConfig(anyString())).thenThrow(new RuntimeException("Parse error"));
        MockResponse response = mockService.getMockResponseByPathAndMethod("/test", "GET");
        assertNotNull(response);
        assertEquals(500, getStatusCode(response));
        assertTrue(getBody(response).contains("Internal server error"));
    }

    @Test
    void testCreateHttpHeadersWithHeaders() {
        Endpoints endpoint = new Endpoints("/test", 200, "desc");
        List<Headers> headers = Collections.singletonList(new Headers(endpoint, "X-Test", "value"));
        HttpHeaders httpHeaders = invokeCreateHttpHeaders(headers);
        assertTrue(httpHeaders.containsKey("X-Test"));
    }

    @Test
    void testCreateHttpHeadersWithNoHeaders() {
        HttpHeaders httpHeaders = invokeCreateHttpHeaders(Collections.emptyList());
        assertTrue(httpHeaders.containsKey("Content-Type"));
    }

    // Helper to access private method for test coverage
    private HttpHeaders invokeCreateHttpHeaders(List<Headers> headers) {
        try {
            java.lang.reflect.Method m = MockServiceImpl.class.getDeclaredMethod("createHttpHeaders", List.class);
            m.setAccessible(true);
            return (HttpHeaders) m.invoke(mockService, headers);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
} 