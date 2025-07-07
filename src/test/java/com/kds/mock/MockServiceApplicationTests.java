package com.kds.mock;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
@ActiveProfiles("test")
class MockServiceApplicationTests {

    @Test
    void contextLoads() {
        // This test verifies that the Spring context loads successfully
        // It covers the main method and application startup
    }

    @Test
    void testMainMethod() {
        // Test that the main method can be called without throwing exceptions
        assertDoesNotThrow(() -> {
            MockServiceApplication.main(new String[]{});
        });
    }
} 