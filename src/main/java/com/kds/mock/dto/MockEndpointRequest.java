package com.kds.mock.dto;

import com.kds.mock.entity.Headers;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpMethod;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class MockEndpointRequest {

    private String path;

    private HttpMethod method;

    private int statusCode;

    private Map<String, String> responseHeaders;

    private String body;

    private String contentType;

    private String description;
}
