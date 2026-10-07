package com.mohit.aiagent.service;

import com.mohit.aiagent.model.IncidentEvent;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.*;

class KafkaProducerServiceTest {
    @Test
    void publishesIncidentToKafkaAsJson() {
        KafkaTemplate<String, String> template = mock(KafkaTemplate.class);
        KafkaProducerService service = new KafkaProducerService(template, "incident-events");
        IncidentEvent event = new IncidentEvent("INC-1", "payments", "HIGH", "timeout");

        service.publish(event);

        verify(template).send(eq("incident-events"), eq("INC-1"),
                eq("{\"incidentId\":\"INC-1\",\"service\":\"payments\",\"severity\":\"HIGH\",\"message\":\"timeout\"}"));
    }
}
