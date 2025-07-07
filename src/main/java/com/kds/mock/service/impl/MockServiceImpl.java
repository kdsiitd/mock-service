package com.kds.mock.service.impl;

import com.kds.mock.dto.MockResponse;
import com.kds.mock.dto.LoadTestConfig;
import com.kds.mock.entity.Endpoints;
import com.kds.mock.entity.Headers;
import com.kds.mock.entity.Responses;
import com.kds.mock.repository.EndpointsRepository;
import com.kds.mock.repository.HeadersRepository;
import com.kds.mock.repository.ResponsesRepository;
import com.kds.mock.service.MockService;
import com.kds.mock.service.LoadTestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MockServiceImpl implements MockService {

    private final EndpointsRepository endpointsRepository;
    private final HeadersRepository headersRepository;
    private final ResponsesRepository responsesRepository;
    private final LoadTestService loadTestService;

    @Override
    public MockResponse getMockResponseByPathAndMethod(String path, String method) {
        Endpoints endpoint = endpointsRepository.findEndpointByPath(path);

        if (endpoint == null) {
            return null;
        }

        try {
            LoadTestConfig loadTestConfig = loadTestService.parseLoadTestConfig(endpoint.getLoadTestConfig());
            
            // Check rate limiting first
            MockResponse rateLimitResponse = checkRateLimit(path, loadTestConfig);
            if (rateLimitResponse != null) {
                return rateLimitResponse;
            }
            
            // Check for timeout simulation
            MockResponse timeoutResponse = checkTimeout(loadTestConfig);
            if (timeoutResponse != null) {
                return timeoutResponse;
            }
            
            // Simulate latency
            simulateLatency(loadTestConfig);
            
            // Check for error simulation
            MockResponse errorResponse = checkErrorSimulation(loadTestConfig);
            if (errorResponse != null) {
                return errorResponse;
            }
            
            // Return normal response
            return createNormalResponse(endpoint, method);
            
        } catch (Exception e) {
            log.error("Error processing mock response for path: {} method: {}", path, method, e);
            return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error");
        }
    }

    private MockResponse checkRateLimit(String path, LoadTestConfig loadTestConfig) {
        if (loadTestService.isRateLimitExceeded(path, loadTestConfig)) {
            LoadTestConfig.RateLimitConfig rateLimitConfig = getRateLimitConfig(loadTestConfig);
            
            HttpHeaders rateLimitHeaders = new HttpHeaders();
            rateLimitHeaders.add("Retry-After", "1");
            rateLimitHeaders.add("X-RateLimit-Limit", String.valueOf(rateLimitConfig.getRequestsPerSecond()));
            rateLimitHeaders.add("X-RateLimit-Remaining", "0");
            
            return new MockResponse(
                rateLimitConfig.getRateLimitStatusCode(),
                rateLimitHeaders,
                "{\"error\": \"Rate limit exceeded\", \"message\": \"Too many requests\"}"
            );
        }
        return null;
    }

    private MockResponse checkTimeout(LoadTestConfig loadTestConfig) {
        if (loadTestService.shouldTimeout(loadTestConfig)) {
            LoadTestConfig.TimeoutConfig timeoutConfig = getTimeoutConfig(loadTestConfig);
            
            // Simulate timeout by sleeping for the configured timeout duration
            try {
                Thread.sleep(timeoutConfig.getTimeoutMs());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("Timeout simulation interrupted");
            }
            
            return createErrorResponse(
                HttpStatus.REQUEST_TIMEOUT.value(),
                "Request timed out"
            );
        }
        return null;
    }

    private void simulateLatency(LoadTestConfig loadTestConfig) {
        try {
            loadTestService.simulateLatency(loadTestConfig);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Latency simulation interrupted");
        }
    }

    private MockResponse checkErrorSimulation(LoadTestConfig loadTestConfig) {
        if (loadTestService.shouldGenerateError(loadTestConfig)) {
            int errorStatusCode = loadTestService.getRandomErrorStatusCode(loadTestConfig);
            String errorMessage = loadTestService.getErrorMessage(loadTestConfig, errorStatusCode);
            
            return createErrorResponse(errorStatusCode, errorMessage);
        }
        return null;
    }

    private MockResponse createNormalResponse(Endpoints endpoint, String method) {
        List<Headers> headers = headersRepository.findAllByEndpointsId(endpoint.getId());
        HttpHeaders httpHeaders = createHttpHeaders(headers);
        Responses response = responsesRepository.findResponseByEndpointsIdAndMethod(endpoint.getId(), method);
        
        return new MockResponse(
            endpoint.getStatusCode(), 
            httpHeaders,
            response != null ? response.getBody() : null
        );
    }

    private HttpHeaders createHttpHeaders(List<Headers> headers) {
        HttpHeaders httpHeaders = new HttpHeaders();
        
        if (headers != null && !headers.isEmpty()) {
            headers.forEach(header -> httpHeaders.add(header.getName(), header.getValue()));
        } else {
            httpHeaders.add("Content-Type", "application/json");
        }
        
        return httpHeaders;
    }

    private MockResponse createErrorResponse(int statusCode, String message) {
        HttpHeaders errorHeaders = new HttpHeaders();
        errorHeaders.add("Content-Type", "application/json");
        
        String errorBody = String.format(
            "{\"error\": \"%s\", \"message\": \"%s\", \"statusCode\": %d}", 
            HttpStatus.valueOf(statusCode).getReasonPhrase(), 
            message, 
            statusCode
        );
        
        return new MockResponse(statusCode, errorHeaders, errorBody);
    }

    private LoadTestConfig.RateLimitConfig getRateLimitConfig(LoadTestConfig loadTestConfig) {
        return loadTestConfig != null && loadTestConfig.getRateLimitConfig() != null ? 
            loadTestConfig.getRateLimitConfig() : 
            new LoadTestConfig.RateLimitConfig();
    }

    private LoadTestConfig.TimeoutConfig getTimeoutConfig(LoadTestConfig loadTestConfig) {
        return loadTestConfig != null && loadTestConfig.getTimeoutConfig() != null ? 
            loadTestConfig.getTimeoutConfig() : 
            new LoadTestConfig.TimeoutConfig();
    }
}
