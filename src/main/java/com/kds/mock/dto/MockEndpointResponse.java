package com.kds.mock.dto;

import com.kds.mock.entity.Endpoints;
import com.kds.mock.entity.Headers;
import com.kds.mock.entity.Responses;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class MockEndpointResponse {
    private Endpoints endpoints;
    private List<Headers> headers;
    private Responses responses;

    public MockEndpointResponse(Endpoints endpoints, List<Headers> headers, Responses responses) {
        this.endpoints = endpoints;
        this.headers = headers;
        this.responses = responses;
    }
}
