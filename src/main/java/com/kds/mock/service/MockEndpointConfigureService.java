package com.kds.mock.service;

import com.kds.mock.dto.MockEndpointRequest;
import com.kds.mock.dto.MockEndpointResponse;
import com.kds.mock.dto.UpdateMockEndpointRequest;

import java.util.List;

public interface MockEndpointConfigureService {
    MockEndpointResponse saveMockEndpoint(MockEndpointRequest mockEndpointRequest);
    List<MockEndpointResponse> getAllMockEndpoints();
    MockEndpointResponse updateMockEndpoint(String path, String method, MockEndpointRequest request);
    MockEndpointResponse updateMockEndpoint(String path, String method, UpdateMockEndpointRequest request);
    void deleteMockEndpoint(String path, String method);
    MockEndpointResponse getMockEndpoint(String path, String method);
    MockEndpointResponse getMockEndpointById(Long id);
}
