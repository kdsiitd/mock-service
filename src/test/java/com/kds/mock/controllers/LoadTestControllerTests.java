package com.kds.mock.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kds.mock.dto.LoadTestConfig;
import com.kds.mock.service.LoadTestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoadTestController.class)
class LoadTestControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LoadTestService loadTestService;

    @BeforeEach
    void setup() {
        objectMapper.findAndRegisterModules();
    }

    @Test
    void testValidateLoadTestConfig_Success() throws Exception {
        String validConfigJson = """
            {
                "latencyConfig": {
                    "type": "PERCENTILE",
                    "p50": 100,
                    "p95": 300,
                    "p99": 500
                },
                "errorConfig": {
                    "enabled": true,
                    "errorRate": 0.05
                }
            }
            """;

        LoadTestConfig config = objectMapper.readValue(validConfigJson, LoadTestConfig.class);

        when(loadTestService.toJson(any(LoadTestConfig.class))).thenReturn(validConfigJson);
        when(loadTestService.parseLoadTestConfig(anyString())).thenReturn(config);

        mockMvc.perform(post("/load-test/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validConfigJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.message").value("Load test configuration is valid"));
    }

    @Test
    void testValidateLoadTestConfig_InvalidConfig() throws Exception {
        String invalidConfigJson = """
            {
                "latencyConfig": {
                    "type": "PERCENTILE",
                    "p50": 500,
                    "p95": 300
                },
                "errorConfig": {
                    "enabled": true,
                    "errorRate": 1.5
                }
            }
            """;

        LoadTestConfig config = objectMapper.readValue(invalidConfigJson, LoadTestConfig.class);

        when(loadTestService.toJson(any(LoadTestConfig.class))).thenReturn(invalidConfigJson);
        when(loadTestService.parseLoadTestConfig(anyString())).thenReturn(config);

        mockMvc.perform(post("/load-test/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidConfigJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.message").value("Configuration validation failed"));
    }

    @Test
    void testValidateLoadTestConfig_Exception() throws Exception {
        String validConfigJson = """
            {
                "latencyConfig": {
                    "type": "PERCENTILE",
                    "p50": 100,
                    "p95": 300,
                    "p99": 500
                }
            }
            """;

        when(loadTestService.toJson(any(LoadTestConfig.class))).thenThrow(new RuntimeException("Serialization error"));

        mockMvc.perform(post("/load-test/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validConfigJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.message").value("Configuration validation failed: Serialization error"));
    }

    @Test
    void testGetLoadTestStats() throws Exception {
        mockMvc.perform(get("/load-test/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activeEndpoints").exists())
                .andExpect(jsonPath("$.totalRequests").exists())
                .andExpect(jsonPath("$.rateLimitedRequests").exists())
                .andExpect(jsonPath("$.timeoutRequests").exists())
                .andExpect(jsonPath("$.errorRequests").exists())
                .andExpect(jsonPath("$.averageLatency").exists())
                .andExpect(jsonPath("$.maxLatency").exists())
                .andExpect(jsonPath("$.minLatency").exists());
    }

    @Test
    void testResetLoadTestState() throws Exception {
        mockMvc.perform(post("/load-test/reset"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Load testing state reset successfully"))
                .andExpect(jsonPath("$.resetTimestamp").exists());
    }
} 