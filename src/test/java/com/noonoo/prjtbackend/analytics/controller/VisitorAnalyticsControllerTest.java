package com.noonoo.prjtbackend.analytics.controller;

import com.noonoo.prjtbackend.analytics.service.VisitorAnalyticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class VisitorAnalyticsControllerTest {

    private MockMvc mockMvc;

    @Mock
    private VisitorAnalyticsService visitorAnalyticsService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new VisitorAnalyticsController(visitorAnalyticsService))
                .build();
    }

    @Test
    void heartbeatAcceptsJson() throws Exception {
        mockMvc.perform(post("/api/analytics/heartbeat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"visitorKey\":\"vk-test-1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(visitorAnalyticsService).heartbeat(eq("vk-test-1"), any());
    }
}
