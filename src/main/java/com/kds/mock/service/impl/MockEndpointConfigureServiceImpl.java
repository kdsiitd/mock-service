package com.kds.mock.service.impl;

import com.kds.mock.dto.MockEndpointRequest;
import com.kds.mock.dto.MockEndpointResponse;
import com.kds.mock.entity.Endpoints;
import com.kds.mock.entity.Headers;
import com.kds.mock.entity.Responses;
import com.kds.mock.repository.EndpointsRepository;
import com.kds.mock.repository.HeadersRepository;
import com.kds.mock.repository.ResponsesRepository;
import com.kds.mock.service.MockEndpointConfigureService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MockEndpointConfigureServiceImpl implements MockEndpointConfigureService {

    @Autowired
    private EndpointsRepository endpointsRepository;

    @Autowired
    private HeadersRepository headersRepository;

    @Autowired
    private ResponsesRepository responsesRepository;

    @Override
    @Transactional
    public MockEndpointResponse saveMockEndpoint(MockEndpointRequest request) {

        try {
            Endpoints endpoints = new Endpoints(request.getPath(), request.getStatusCode(), request.getDescription());

            endpointsRepository.save(endpoints);

            List<Headers> headers = new ArrayList<>();
            request.getResponseHeaders().forEach((key, value) -> {
                Headers header = new Headers(endpoints, key, value);
                headersRepository.save(header);

                headers.add(header);
            });

            Responses responses = new Responses(endpoints, request.getMethod().name(), request.getContentType(),
                    request.getBody());
            responsesRepository.save(responses);

            return new MockEndpointResponse(endpoints, headers, responses);
        } catch (Exception ex) {
            System.out.println("Error in saving MockEndpoint: " + ex.getMessage());
        }

        return null;
    }
}
