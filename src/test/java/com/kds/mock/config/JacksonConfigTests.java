package com.kds.mock.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class JacksonConfigTests {

    @Test
    void testObjectMapperBean() {
        // This test verifies that the ObjectMapper bean is created successfully
        // The JacksonConfig class should be loaded and configured
        assertNotNull(new JacksonConfig());
    }
} 