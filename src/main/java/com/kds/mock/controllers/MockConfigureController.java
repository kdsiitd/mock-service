package com.kds.mock.controllers;

import com.kds.mock.dto.MockEndpointRequest;
import com.kds.mock.dto.MockEndpointResponse;
import com.kds.mock.service.MockEndpointConfigureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/endpoints")
public class MockConfigureController {

    @Autowired
    private MockEndpointConfigureService mockEndpointConfigureService;

    @PostMapping("/configure")
    public ResponseEntity<MockEndpointResponse> configureMockEndpoint(@RequestBody MockEndpointRequest request) {

        MockEndpointResponse response = mockEndpointConfigureService.saveMockEndpoint(request);

        if (response != null)
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        else
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

    }

    @GetMapping("/list")
    public List<?> fetchMockedEndpoints() {
        return null;
    }
}