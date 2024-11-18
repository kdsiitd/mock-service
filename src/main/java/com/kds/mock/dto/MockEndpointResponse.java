package com.kds.mock.dto;

import com.kds.mock.entity.Endpoints;
import com.kds.mock.entity.Headers;
import com.kds.mock.entity.Responses;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class MockEndpointResponse {

    private Endpoints endpoints;

    private List<Headers> headers;

    private Responses responses;
}
