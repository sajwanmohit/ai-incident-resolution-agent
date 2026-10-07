package com.mohit.aiagent.controller;

import com.mohit.aiagent.model.IncidentEvent;
import com.mohit.aiagent.service.KafkaProducerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(IncidentController.class)
class IncidentControllerTest {
    @Autowired MockMvc mockMvc;
    @MockBean KafkaProducerService producerService;

    @Test
    void acceptsValidIncident() throws Exception {
        mockMvc.perform(post("/api/v1/incidents")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"incidentId\":\"INC-1\",\"service\":\"payments\",\"severity\":\"HIGH\",\"message\":\"timeout\"}"))
                .andExpect(status().isAccepted());
        verify(producerService).publish(any(IncidentEvent.class));
    }
}
