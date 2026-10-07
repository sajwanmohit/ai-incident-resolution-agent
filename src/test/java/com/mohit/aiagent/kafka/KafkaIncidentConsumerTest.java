package com.mohit.aiagent.kafka;

import com.mohit.aiagent.model.IncidentEvent;
import com.mohit.aiagent.service.IncidentResolutionService;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class KafkaIncidentConsumerTest {
    @Test
    void consumerMethodCanProcessJsonMessage() {
        IncidentResolutionService resolutionService = mock(IncidentResolutionService.class);
        KafkaIncidentConsumer consumer = new KafkaIncidentConsumer(resolutionService);

        consumer.consume("{\"incidentId\":\"INC-1\",\"service\":\"payments\",\"severity\":\"HIGH\",\"message\":\"timeout\"}");

        verify(resolutionService).analyze(argThat(event ->
                event.incidentId().equals("INC-1")
                        && event.service().equals("payments")
                        && event.severity().equals("HIGH")
                        && event.message().equals("timeout")));
    }
}
