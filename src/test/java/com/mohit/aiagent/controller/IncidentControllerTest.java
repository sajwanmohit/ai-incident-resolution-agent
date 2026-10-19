package com.mohit.aiagent.controller;

import com.mohit.aiagent.model.IncidentEvent;
import com.mohit.aiagent.service.IncidentAnalysisResult;
import com.mohit.aiagent.service.IncidentResolutionService;
import com.mohit.aiagent.service.KafkaProducerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(IncidentController.class)
class IncidentControllerTest {
    @Autowired MockMvc mockMvc;
    @MockBean KafkaProducerService producerService;
    @MockBean IncidentResolutionService resolutionService;

    @Test
    void acceptsValidIncident() throws Exception {
        when(resolutionService.analyze(any(IncidentEvent.class)))
                .thenReturn(new IncidentAnalysisResult(
                        "HIGH",
                        0.9,
                        "Payment API timeout",
                        List.of("Check payment provider latency"),
                        "Upstream payment API latency caused the timeout.",
                        List.of("Incident severity is HIGH", "Service is payments", "Message indicates timeout")
                ));

        mockMvc.perform(post("/api/v1/incidents")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"incidentId\":\"INC-1\",\"service\":\"payments\",\"severity\":\"HIGH\",\"message\":\"timeout\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.severity").value("HIGH"))
                .andExpect(jsonPath("$.summary").value("Payment API timeout"))
                .andExpect(jsonPath("$.recommendedActions[0]").value("Check payment provider latency"));

        verify(producerService).publish(any(IncidentEvent.class));
        verify(resolutionService).analyze(any(IncidentEvent.class));
    }

    @Test
    void allowsConfiguredVercelOriginAndPreflight() throws Exception {
        mockMvc.perform(options("/api/v1/incidents")
                        .header(HttpHeaders.ORIGIN, "https://ai-incident-dashboard-ashy.vercel.app")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "Content-Type, Authorization"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "https://ai-incident-dashboard-ashy.vercel.app"))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET,POST,PUT,PATCH,DELETE,OPTIONS"))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "Content-Type, Authorization"));
    }

    @Test
    void rejectsUnapprovedOrigin() throws Exception {
        mockMvc.perform(post("/api/v1/incidents")
                        .header(HttpHeaders.ORIGIN, "https://not-allowed.example.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"incidentId\":\"INC-2\",\"service\":\"payments\",\"severity\":\"HIGH\",\"message\":\"timeout\"}"))
                .andExpect(status().isForbidden());
    }
}
