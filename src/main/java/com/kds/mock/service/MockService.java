package com.kds.mock.service;

import com.kds.mock.dto.MockResponse;

public interface MockService {
    MockResponse getMockResponseByPathAndMethod(String path, String method);
}
