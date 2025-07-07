package com.kds.mock.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HealthController.class)
class HealthControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetHealth_Success() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.database").value("UP"))
                .andExpect(jsonPath("$.version").value("1.0.0"))
                .andExpect(jsonPath("$.uptime").exists())
                .andExpect(jsonPath("$.activeEndpoints").exists())
                .andExpect(jsonPath("$.totalRequests").exists());
    }

    @Test
    void testGetHealth_ReturnsValidJsonStructure() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").isString())
                .andExpect(jsonPath("$.timestamp").isString())
                .andExpect(jsonPath("$.database").isString())
                .andExpect(jsonPath("$.version").isString())
                .andExpect(jsonPath("$.uptime").isString())
                .andExpect(jsonPath("$.activeEndpoints").isNumber())
                .andExpect(jsonPath("$.totalRequests").isNumber());
    }

    @Test
    void testGetSystemInfo_Success() throws Exception {
        mockMvc.perform(get("/health/info"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.jvm").exists())
                .andExpect(jsonPath("$.jvm.version").exists())
                .andExpect(jsonPath("$.jvm.vendor").exists())
                .andExpect(jsonPath("$.jvm.memory").exists())
                .andExpect(jsonPath("$.jvm.memory.total").exists())
                .andExpect(jsonPath("$.jvm.memory.free").exists())
                .andExpect(jsonPath("$.jvm.memory.used").exists())
                .andExpect(jsonPath("$.system").exists())
                .andExpect(jsonPath("$.system.os").exists())
                .andExpect(jsonPath("$.system.arch").exists())
                .andExpect(jsonPath("$.system.processors").exists())
                .andExpect(jsonPath("$.application").exists())
                .andExpect(jsonPath("$.application.name").value("mock-service"))
                .andExpect(jsonPath("$.application.version").value("1.0.0"))
                .andExpect(jsonPath("$.application.startTime").exists());
    }

    @Test
    void testGetSystemInfo_ReturnsValidJsonStructure() throws Exception {
        mockMvc.perform(get("/health/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jvm").isMap())
                .andExpect(jsonPath("$.jvm.version").isString())
                .andExpect(jsonPath("$.jvm.vendor").isString())
                .andExpect(jsonPath("$.jvm.memory").isMap())
                .andExpect(jsonPath("$.jvm.memory.total").isString())
                .andExpect(jsonPath("$.jvm.memory.free").isString())
                .andExpect(jsonPath("$.jvm.memory.used").isString())
                .andExpect(jsonPath("$.system").isMap())
                .andExpect(jsonPath("$.system.os").isString())
                .andExpect(jsonPath("$.system.arch").isString())
                .andExpect(jsonPath("$.system.processors").isNumber())
                .andExpect(jsonPath("$.application").isMap())
                .andExpect(jsonPath("$.application.name").isString())
                .andExpect(jsonPath("$.application.version").isString())
                .andExpect(jsonPath("$.application.startTime").isString());
    }

    @Test
    void testGetSystemInfo_MemoryValuesAreFormatted() throws Exception {
        mockMvc.perform(get("/health/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jvm.memory.total").value(org.hamcrest.Matchers.matchesPattern(".*[BKMGT].*")))
                .andExpect(jsonPath("$.jvm.memory.free").value(org.hamcrest.Matchers.matchesPattern(".*[BKMGT].*")))
                .andExpect(jsonPath("$.jvm.memory.used").value(org.hamcrest.Matchers.matchesPattern(".*[BKMGT].*")));
    }

    @Test
    void testGetSystemInfo_ProcessorCountIsPositive() throws Exception {
        mockMvc.perform(get("/health/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.system.processors").value(org.hamcrest.Matchers.greaterThan(0)));
    }

    @Test
    void testHealthEndpoints_ReturnCorrectContentType() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));

        mockMvc.perform(get("/health/info"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    void testHealthEndpoints_ReturnValidJson() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json("{}", false)); // Validate JSON structure

        mockMvc.perform(get("/health/info"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json("{}", false)); // Validate JSON structure
    }
} 