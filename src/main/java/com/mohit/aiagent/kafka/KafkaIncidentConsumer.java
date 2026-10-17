package com.mohit.aiagent.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mohit.aiagent.model.IncidentEvent;
import com.mohit.aiagent.service.IncidentAnalysisResult;
import com.mohit.aiagent.service.IncidentResolutionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaIncidentConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(KafkaIncidentConsumer.class);

    private final IncidentResolutionService resolutionService;
    private final ObjectMapper objectMapper;

    public KafkaIncidentConsumer(IncidentResolutionService resolutionService) {
        this.resolutionService = resolutionService;
        this.objectMapper = new ObjectMapper();
    }

    @KafkaListener(
            topics = "${app.kafka.topic}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(String message) {
        log.info("Kafka consumer received raw message: {}", message);
        try {
            IncidentEvent event = objectMapper.readValue(message, IncidentEvent.class);
            log.info("Consumed incident event: {}", event);

            IncidentAnalysisResult analysis = resolutionService.analyze(event);
            log.info("Incident analysis result: {}", analysis);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse incident event: {}", message, e);
            throw new IllegalArgumentException("Invalid incident event payload", e);
        }
    }
}