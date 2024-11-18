package com.kds.mock.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpHeaders;

@Getter
@Setter
@AllArgsConstructor
public class MockResponse {

    private int statusCode;

    private HttpHeaders headers;

    private String body;
}
