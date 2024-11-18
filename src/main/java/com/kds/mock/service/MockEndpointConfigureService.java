package com.kds.mock.service;

import com.kds.mock.dto.MockEndpointRequest;
import com.kds.mock.dto.MockEndpointResponse;

public interface MockEndpointConfigureService {
    MockEndpointResponse saveMockEndpoint(MockEndpointRequest mockEndpointRequest);
}
