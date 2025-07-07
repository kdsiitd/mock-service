package com.kds.mock;

import com.kds.mock.dto.MockResponse;
import com.kds.mock.entity.Endpoints;
import com.kds.mock.entity.Headers;
import com.kds.mock.entity.Responses;
import com.kds.mock.repository.EndpointsRepository;
import com.kds.mock.repository.HeadersRepository;
import com.kds.mock.repository.ResponsesRepository;
import com.kds.mock.service.MockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MockServiceTests {

    @Autowired
    private MockService mockService;

    @MockBean
    private EndpointsRepository endpointsRepository;

    @MockBean
    private HeadersRepository headersRepository;

    @MockBean
    private ResponsesRepository responsesRepository;

    private Endpoints testEndpoint;
    private List<Headers> testHeaders;
    private Responses testResponse;

    @BeforeEach
    void setUp() {
        testEndpoint = new Endpoints("/test/path", 200, "Test endpoint");
        testEndpoint.setId(1L);

        testHeaders = new ArrayList<>();
        testHeaders.add(new Headers(testEndpoint, HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));
        testHeaders.add(new Headers(testEndpoint, "Custom-Header", "custom-value"));

        testResponse = new Responses(testEndpoint, HttpMethod.GET.name(), 
            MediaType.APPLICATION_JSON_VALUE, "{\"name\": \"test\"}");
    }

    @Test
    void testMockResponseWithValidEndpoint() {
        when(endpointsRepository.findEndpointByPath("/test/path")).thenReturn(testEndpoint);
        when(headersRepository.findAllByEndpointsId(1L)).thenReturn(testHeaders);
        when(responsesRepository.findResponseByEndpointsIdAndMethod(1L, HttpMethod.GET.name()))
                .thenReturn(testResponse);

        MockResponse mockResponse = mockService.getMockResponseByPathAndMethod("/test/path", HttpMethod.GET.name());

        assertNotNull(mockResponse);
        assertAll(
            () -> assertEquals(200, mockResponse.getStatusCode()),
            () -> assertNotNull(mockResponse.getHeaders()),
            () -> assertEquals(MediaType.APPLICATION_JSON_VALUE, 
                Objects.requireNonNull(mockResponse.getHeaders().getContentType()).toString()),
            () -> assertEquals("custom-value", mockResponse.getHeaders().getFirst("Custom-Header")),
            () -> assertEquals("{\"name\": \"test\"}", mockResponse.getBody())
        );
    }

    @Test
    void testMockResponseWithNonExistentEndpoint() {
        when(endpointsRepository.findEndpointByPath("/non-existent")).thenReturn(null);

        MockResponse mockResponse = mockService.getMockResponseByPathAndMethod("/non-existent", HttpMethod.GET.name());

        assertNull(mockResponse);
    }

    @Test
    void testMockResponseWithNoHeaders() {
        when(endpointsRepository.findEndpointByPath("/test/path")).thenReturn(testEndpoint);
        when(headersRepository.findAllByEndpointsId(1L)).thenReturn(new ArrayList<>());
        when(responsesRepository.findResponseByEndpointsIdAndMethod(1L, HttpMethod.GET.name()))
                .thenReturn(testResponse);

        MockResponse mockResponse = mockService.getMockResponseByPathAndMethod("/test/path", HttpMethod.GET.name());

        assertNotNull(mockResponse);
        assertAll(
            () -> assertEquals(200, mockResponse.getStatusCode()),
            () -> assertNotNull(mockResponse.getHeaders()),
            () -> assertEquals(MediaType.APPLICATION_JSON_VALUE,
                Objects.requireNonNull(mockResponse.getHeaders().getContentType()).toString()),
            () -> assertEquals("{\"name\": \"test\"}", mockResponse.getBody())
        );
    }

    @Test
    void testMockResponseWithDifferentHttpMethod() {
        when(endpointsRepository.findEndpointByPath("/test/path")).thenReturn(testEndpoint);
        when(headersRepository.findAllByEndpointsId(1L)).thenReturn(testHeaders);
        when(responsesRepository.findResponseByEndpointsIdAndMethod(1L, HttpMethod.POST.name()))
                .thenReturn(new Responses(testEndpoint, HttpMethod.POST.name(), 
                    MediaType.APPLICATION_JSON_VALUE, "{\"method\": \"POST\"}"));

        MockResponse mockResponse = mockService.getMockResponseByPathAndMethod("/test/path", HttpMethod.POST.name());

        assertNotNull(mockResponse);
        assertAll(
            () -> assertEquals(200, mockResponse.getStatusCode()),
            () -> assertNotNull(mockResponse.getHeaders()),
            () -> assertEquals("{\"method\": \"POST\"}", mockResponse.getBody())
        );
    }

    @Test
    void testMockResponseWithErrorStatusCode() {
        Endpoints errorEndpoint = new Endpoints("/error/path", 500, "Error endpoint");
        errorEndpoint.setId(2L);
        
        when(endpointsRepository.findEndpointByPath("/error/path")).thenReturn(errorEndpoint);
        when(headersRepository.findAllByEndpointsId(2L)).thenReturn(testHeaders);
        when(responsesRepository.findResponseByEndpointsIdAndMethod(2L, HttpMethod.GET.name()))
                .thenReturn(new Responses(errorEndpoint, HttpMethod.GET.name(), 
                    MediaType.APPLICATION_JSON_VALUE, "{\"error\": \"Internal Server Error\"}"));

        MockResponse mockResponse = mockService.getMockResponseByPathAndMethod("/error/path", HttpMethod.GET.name());

        assertNotNull(mockResponse);
        assertAll(
            () -> assertEquals(500, mockResponse.getStatusCode()),
            () -> assertNotNull(mockResponse.getHeaders()),
            () -> assertEquals("{\"error\": \"Internal Server Error\"}", mockResponse.getBody())
        );
    }
}
