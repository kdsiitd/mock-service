package com.kds.mock.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpHeaders;

@Getter
@Setter
@NoArgsConstructor
public class MockResponse {
    private int statusCode;
    private HttpHeaders headers;
    private String body;

    public MockResponse(int statusCode, HttpHeaders headers, String body) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.body = body;
    }
}
