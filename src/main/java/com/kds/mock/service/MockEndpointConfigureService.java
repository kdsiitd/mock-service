package com.kds.mock.service;

import com.kds.mock.dto.MockEndpointRequest;
import com.kds.mock.dto.MockEndpointResponse;

import java.util.List;

public interface MockEndpointConfigureService {
    MockEndpointResponse saveMockEndpoint(MockEndpointRequest mockEndpointRequest);
    List<MockEndpointResponse> getAllMockEndpoints();
}
