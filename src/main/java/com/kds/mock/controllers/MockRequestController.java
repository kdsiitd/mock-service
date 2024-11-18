package com.kds.mock.controllers;

import com.kds.mock.dto.MockResponse;
import com.kds.mock.service.MockService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class MockRequestController {

    @Autowired
    private MockService mockEndpointService;

    @RequestMapping(value = "/**")
    public ResponseEntity<?> handleMockRequest(HttpServletRequest request) {
        MockResponse response = mockEndpointService.getMockResponseByPathAndMethod(request.getRequestURI(), request.getMethod());
        if (response == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Mock endpoint not configured.");
        }
        return ResponseEntity
                .status(response.getStatusCode())
                .headers(response.getHeaders())
                .body(response.getBody());
    }
}
