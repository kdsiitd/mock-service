package com.kds.mock.service.impl;

import com.kds.mock.dto.MockResponse;
import com.kds.mock.entity.Endpoints;
import com.kds.mock.entity.Headers;
import com.kds.mock.entity.Responses;
import com.kds.mock.repository.EndpointsRepository;
import com.kds.mock.repository.HeadersRepository;
import com.kds.mock.repository.ResponsesRepository;
import com.kds.mock.service.MockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MockServiceImpl implements MockService {

    @Autowired
    private EndpointsRepository endpointsRepository;

    @Autowired
    private HeadersRepository headersRepository;

    @Autowired
    private ResponsesRepository responsesRepository;

    @Override
    public MockResponse getMockResponseByPathAndMethod(String path, String method) {
        Endpoints endpoint = endpointsRepository.findEndpointByPath(path);

        if (endpoint != null) {
            List<Headers> headers = headersRepository.findAllByEndpointsId(endpoint.getId());

            HttpHeaders httpHeaders;
            if (headers != null) {
                httpHeaders = new HttpHeaders();
                headers.forEach(header -> httpHeaders.add(header.getName(), header.getValue()));
            } else {
                httpHeaders = null;
            }

            Responses response = responsesRepository.findResponseByEndpointsIdAndMethod(endpoint.getId(), method);

            return new MockResponse(endpoint.getStatusCode(), httpHeaders,
                    response != null ? response.getBody() : null);
        }
        return null;
    }
}
