package com.kds.mock.service.impl;

import com.kds.mock.dto.MockEndpointRequest;
import com.kds.mock.dto.MockEndpointResponse;
import com.kds.mock.dto.UpdateMockEndpointRequest;
import com.kds.mock.dto.LoadTestConfig;
import com.kds.mock.entity.Endpoints;
import com.kds.mock.entity.Headers;
import com.kds.mock.entity.Responses;
import com.kds.mock.repository.EndpointsRepository;
import com.kds.mock.repository.HeadersRepository;
import com.kds.mock.repository.ResponsesRepository;
import com.kds.mock.service.MockEndpointConfigureService;
import com.kds.mock.service.LoadTestService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MockEndpointConfigureServiceImpl implements MockEndpointConfigureService {

    private final EndpointsRepository endpointsRepository;
    private final HeadersRepository headersRepository;
    private final ResponsesRepository responsesRepository;
    private final LoadTestService loadTestService;

    @Override
    @Transactional
    public MockEndpointResponse saveMockEndpoint(MockEndpointRequest request) {
        try {
            String loadTestConfigJson = serializeLoadTestConfig(request.getLoadTestConfig());
            
            Endpoints endpoints = createEndpoint(request, loadTestConfigJson);
            endpointsRepository.save(endpoints);

            List<Headers> headers = createHeaders(request, endpoints);
            Responses responses = createResponse(request, endpoints);
            
            return new MockEndpointResponse(endpoints, headers, responses, request.getLoadTestConfig());
        } catch (Exception ex) {
            log.error("Error saving MockEndpoint: {}", ex.getMessage(), ex);
            throw new RuntimeException("Failed to save mock endpoint", ex);
        }
    }

    @Override
    @Transactional
    public List<MockEndpointResponse> getAllMockEndpoints() {
        List<Endpoints> endpoints = endpointsRepository.findAll();
        return endpoints.stream()
                .map(this::createMockEndpointResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MockEndpointResponse updateMockEndpoint(String path, String method, MockEndpointRequest request) {
        try {
            // Find existing endpoint
            Endpoints existingEndpoint = endpointsRepository.findEndpointByPath(path);
            if (existingEndpoint == null) {
                throw new IllegalArgumentException("Mock endpoint not found for path: " + path + " and method: " + method);
            }

            // Find the response for the specific method
            List<Responses> responses = responsesRepository.findAll().stream()
                    .filter(r -> r.getEndpoints().getId().equals(existingEndpoint.getId()) && 
                                r.getMethod().equalsIgnoreCase(method))
                    .collect(Collectors.toList());

            if (responses.isEmpty()) {
                throw new IllegalArgumentException("Mock endpoint not found for path: " + path + " and method: " + method);
            }

            Responses existingResponse = responses.get(0);

            // Update endpoint fields if provided
            if (request.getStatusCode() != null) {
                existingEndpoint.setStatusCode(request.getStatusCode());
            }
            if (request.getDescription() != null) {
                existingEndpoint.setDescription(request.getDescription());
            }
            if (request.getLoadTestConfig() != null) {
                String loadTestConfigJson = serializeLoadTestConfig(request.getLoadTestConfig());
                existingEndpoint.setLoadTestConfig(loadTestConfigJson);
            }

            // Update response fields if provided
            if (request.getContentType() != null) {
                existingResponse.setContentType(request.getContentType());
            }
            if (request.getBody() != null) {
                existingResponse.setBody(request.getBody());
            }

            // Update headers if provided
            if (request.getResponseHeaders() != null) {
                // Remove existing headers
                List<Headers> existingHeaders = headersRepository.findAllByEndpointsId(existingEndpoint.getId());
                headersRepository.deleteAll(existingHeaders);

                // Create new headers
                List<Headers> newHeaders = new ArrayList<>();
                request.getResponseHeaders().forEach((key, value) -> {
                    Headers header = new Headers(existingEndpoint, key, value);
                    headersRepository.save(header);
                    newHeaders.add(header);
                });
            }

            // Save updated entities
            endpointsRepository.save(existingEndpoint);
            responsesRepository.save(existingResponse);

            return createMockEndpointResponse(existingEndpoint);
        } catch (Exception ex) {
            log.error("Error updating MockEndpoint: {}", ex.getMessage(), ex);
            throw new RuntimeException("Failed to update mock endpoint", ex);
        }
    }

    @Override
    @Transactional
    public MockEndpointResponse updateMockEndpoint(String path, String method, UpdateMockEndpointRequest request) {
        try {
            // Find existing endpoint
            Endpoints existingEndpoint = endpointsRepository.findEndpointByPath(path);
            if (existingEndpoint == null) {
                throw new IllegalArgumentException("Mock endpoint not found for path: " + path + " and method: " + method);
            }

            // Find the response for the specific method
            List<Responses> responses = responsesRepository.findAll().stream()
                    .filter(r -> r.getEndpoints().getId().equals(existingEndpoint.getId()) && 
                                r.getMethod().equalsIgnoreCase(method))
                    .collect(Collectors.toList());

            if (responses.isEmpty()) {
                throw new IllegalArgumentException("Mock endpoint not found for path: " + path + " and method: " + method);
            }

            Responses existingResponse = responses.get(0);

            // Update endpoint fields if provided
            if (request.getStatusCode() != null) {
                existingEndpoint.setStatusCode(request.getStatusCode());
            }
            if (request.getDescription() != null) {
                existingEndpoint.setDescription(request.getDescription());
            }
            if (request.getLoadTestConfig() != null) {
                String loadTestConfigJson = serializeLoadTestConfig(request.getLoadTestConfig());
                existingEndpoint.setLoadTestConfig(loadTestConfigJson);
            }

            // Update response fields if provided
            if (request.getContentType() != null) {
                existingResponse.setContentType(request.getContentType());
            }
            if (request.getBody() != null) {
                existingResponse.setBody(request.getBody());
            }

            // Update headers if provided
            if (request.getResponseHeaders() != null) {
                // Remove existing headers
                List<Headers> existingHeaders = headersRepository.findAllByEndpointsId(existingEndpoint.getId());
                headersRepository.deleteAll(existingHeaders);

                // Create new headers
                List<Headers> newHeaders = new ArrayList<>();
                request.getResponseHeaders().forEach((key, value) -> {
                    Headers header = new Headers(existingEndpoint, key, value);
                    headersRepository.save(header);
                    newHeaders.add(header);
                });
            }

            // Save updated entities
            endpointsRepository.save(existingEndpoint);
            responsesRepository.save(existingResponse);

            return createMockEndpointResponse(existingEndpoint);
        } catch (Exception ex) {
            log.error("Error updating MockEndpoint: {}", ex.getMessage(), ex);
            throw new RuntimeException("Failed to update mock endpoint", ex);
        }
    }

    @Override
    @Transactional
    public void deleteMockEndpoint(String path, String method) {
        try {
            // Find existing endpoint
            Endpoints existingEndpoint = endpointsRepository.findEndpointByPath(path);
            if (existingEndpoint == null) {
                throw new IllegalArgumentException("Mock endpoint not found for path: " + path + " and method: " + method);
            }

            // Find the response for the specific method
            List<Responses> responses = responsesRepository.findAll().stream()
                    .filter(r -> r.getEndpoints().getId().equals(existingEndpoint.getId()) && 
                                r.getMethod().equalsIgnoreCase(method))
                    .collect(Collectors.toList());

            if (responses.isEmpty()) {
                throw new IllegalArgumentException("Mock endpoint not found for path: " + path + " and method: " + method);
            }

            // Delete associated headers
            List<Headers> headers = headersRepository.findAllByEndpointsId(existingEndpoint.getId());
            headersRepository.deleteAll(headers);

            // Delete the response
            responsesRepository.deleteAll(responses);

            // Delete the endpoint
            endpointsRepository.delete(existingEndpoint);
        } catch (Exception ex) {
            log.error("Error deleting MockEndpoint: {}", ex.getMessage(), ex);
            throw new RuntimeException("Failed to delete mock endpoint", ex);
        }
    }

    @Override
    @Transactional
    public MockEndpointResponse getMockEndpoint(String path, String method) {
        try {
            // Find existing endpoint
            Endpoints existingEndpoint = endpointsRepository.findEndpointByPath(path);
            if (existingEndpoint == null) {
                throw new IllegalArgumentException("Mock endpoint not found for path: " + path + " and method: " + method);
            }

            // Find the response for the specific method
            List<Responses> responses = responsesRepository.findAll().stream()
                    .filter(r -> r.getEndpoints().getId().equals(existingEndpoint.getId()) && 
                                r.getMethod().equalsIgnoreCase(method))
                    .collect(Collectors.toList());

            if (responses.isEmpty()) {
                throw new IllegalArgumentException("Mock endpoint not found for path: " + path + " and method: " + method);
            }

            return createMockEndpointResponse(existingEndpoint);
        } catch (Exception ex) {
            log.error("Error retrieving MockEndpoint: {}", ex.getMessage(), ex);
            throw new RuntimeException("Failed to retrieve mock endpoint", ex);
        }
    }

    @Override
    @Transactional
    public MockEndpointResponse getMockEndpointById(Long id) {
        try {
            // Find existing endpoint by ID
            Endpoints existingEndpoint = endpointsRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Mock endpoint not found with ID: " + id));

            return createMockEndpointResponse(existingEndpoint);
        } catch (Exception ex) {
            log.error("Error retrieving MockEndpoint by ID: {}", ex.getMessage(), ex);
            throw new RuntimeException("Failed to retrieve mock endpoint by ID", ex);
        }
    }

    private String serializeLoadTestConfig(LoadTestConfig loadTestConfig) {
        if (loadTestConfig == null) {
            return null;
        }
        
        try {
            return loadTestService.toJson(loadTestConfig);
        } catch (Exception e) {
            log.error("Failed to serialize load test config: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to serialize load test configuration: " + e.getMessage(), e);
        }
    }

    private Endpoints createEndpoint(MockEndpointRequest request, String loadTestConfigJson) {
        Endpoints endpoints = new Endpoints(request.getPath(), request.getStatusCode(), request.getDescription());
        endpoints.setLoadTestConfig(loadTestConfigJson);
        return endpoints;
    }

    private List<Headers> createHeaders(MockEndpointRequest request, Endpoints endpoints) {
        List<Headers> headers = new ArrayList<>();
        if (request.getResponseHeaders() != null) {
            request.getResponseHeaders().forEach((key, value) -> {
                Headers header = new Headers(endpoints, key, value);
                headersRepository.save(header);
                headers.add(header);
            });
        }
        return headers;
    }

    private Responses createResponse(MockEndpointRequest request, Endpoints endpoints) {
        Responses responses = new Responses(endpoints, request.getMethod(), request.getContentType(), request.getBody());
        responsesRepository.save(responses);
        return responses;
    }

    private MockEndpointResponse createMockEndpointResponse(Endpoints endpoint) {
        List<Headers> headers = headersRepository.findAllByEndpointsId(endpoint.getId());
        List<Responses> endpointResponses = responsesRepository.findAll().stream()
                .filter(r -> r.getEndpoints().getId().equals(endpoint.getId()))
                .collect(Collectors.toList());

        LoadTestConfig loadTestConfigObj = null;
        if (endpoint.getLoadTestConfig() != null && !endpoint.getLoadTestConfig().isEmpty()) {
            try {
                com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
                loadTestConfigObj = objectMapper.readValue(endpoint.getLoadTestConfig(), LoadTestConfig.class);
            } catch (Exception e) {
                log.warn("Failed to deserialize loadTestConfig for endpoint {}: {}", endpoint.getPath(), e.getMessage());
            }
        }

        if (!endpointResponses.isEmpty()) {
            return new MockEndpointResponse(endpoint, headers, endpointResponses.get(0), loadTestConfigObj);
        }
        
        log.warn("No responses found for endpoint: {}", endpoint.getPath());
        return new MockEndpointResponse(endpoint, headers, null, loadTestConfigObj);
    }
}
