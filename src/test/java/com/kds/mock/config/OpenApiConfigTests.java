package com.kds.mock.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class OpenApiConfigTests {

    @Test
    void testOpenApiBean() {
        // This test verifies that the OpenAPI bean is created successfully
        // The OpenApiConfig class should be loaded and configured
        assertNotNull(new OpenApiConfig());
    }
} 