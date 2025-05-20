package com.kds.mock.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class MockEndpointRequest {

    private String path;

    private String method;

    private int statusCode;

    private Map<String, String> responseHeaders;

    private String body;

    private String contentType;

    private String description;
}
