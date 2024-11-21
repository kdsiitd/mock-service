package com.kds.mock;

import com.kds.mock.dto.MockResponse;
import com.kds.mock.entity.Endpoints;
import com.kds.mock.entity.Headers;
import com.kds.mock.entity.Responses;
import com.kds.mock.repository.EndpointsRepository;
import com.kds.mock.repository.HeadersRepository;
import com.kds.mock.repository.ResponsesRepository;
import com.kds.mock.service.MockService;
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

    @Test
    public void testMockResponse() {
        Endpoints endpoints = new Endpoints("/path", 200, "description");
        endpoints.setId(1L);
        Mockito.when(endpointsRepository.findEndpointByPath("/path")).thenReturn(endpoints);

        List<Headers> headersList = new ArrayList<>();
        headersList.add(new Headers(endpoints, HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        Mockito.when(headersRepository.findAllByEndpointsId(1L)).thenReturn(headersList);

        Mockito.when(responsesRepository.findResponseByEndpointsIdAndMethod(1L, HttpMethod.GET.name()))
                .thenReturn(new Responses(endpoints, HttpMethod.GET.name(), MediaType.APPLICATION_JSON_VALUE,
                        "" + "{\"name\" : \"test\"}"));

        MockResponse mockResponse = mockService.getMockResponseByPathAndMethod("/path", HttpMethod.GET.name());

        assertNotNull(mockResponse);
        assertAll(
                () -> assertEquals(200, mockResponse.getStatusCode()),
                () -> assertNotNull(mockResponse.getHeaders()),
                () -> assertEquals(MediaType.APPLICATION_JSON_VALUE, Objects.requireNonNull(mockResponse.getHeaders().getContentType()).toString()),
                () -> assertEquals("{\"name\" : \"test\"}", mockResponse.getBody())
        );
    }
}
