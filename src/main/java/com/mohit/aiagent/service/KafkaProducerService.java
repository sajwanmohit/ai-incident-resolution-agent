package com.mohit.aiagent.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mohit.aiagent.model.IncidentEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private static final Logger log = LoggerFactory.getLogger(KafkaProducerService.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String topic;
    private final ObjectMapper objectMapper;

    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate,
                                @Value("${app.kafka.topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
        this.objectMapper = new ObjectMapper();
    }

    public void publish(IncidentEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            log.info("Publishing incident event to topic '{}' with key '{}': {}", topic, event.incidentId(), payload);
            kafkaTemplate.send(topic, event.incidentId(), payload);
            log.info("Kafka send invoked successfully for incidentId={}", event.incidentId());
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize incident event: {}", event, e);
            throw new IllegalStateException("Failed to serialize incident event", e);
        }
    }
}
