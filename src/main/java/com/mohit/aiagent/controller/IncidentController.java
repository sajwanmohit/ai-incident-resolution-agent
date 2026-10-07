package com.mohit.aiagent.controller;

import com.mohit.aiagent.model.IncidentEvent;
import com.mohit.aiagent.service.KafkaProducerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/incidents")
public class IncidentController {

    private final KafkaProducerService producerService;

    public IncidentController(KafkaProducerService producerService) {
        this.producerService = producerService;
    }

    @PostMapping
    public ResponseEntity<Void> publish(@Valid @RequestBody IncidentEvent event) {
        producerService.publish(event);
        return ResponseEntity.accepted().build();
    }
}
