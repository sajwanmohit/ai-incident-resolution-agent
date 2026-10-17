package com.mohit.aiagent.controller;

import com.mohit.aiagent.model.IncidentEvent;
import com.mohit.aiagent.service.IncidentAnalysisResult;
import com.mohit.aiagent.service.IncidentResolutionService;
import com.mohit.aiagent.service.KafkaProducerService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/incidents")
public class IncidentController {

    private static final Logger log = LoggerFactory.getLogger(IncidentController.class);

    private final KafkaProducerService producerService;
    private final IncidentResolutionService resolutionService;

    public IncidentController(KafkaProducerService producerService, IncidentResolutionService resolutionService) {
        this.producerService = producerService;
        this.resolutionService = resolutionService;
    }

    @PostMapping
    public ResponseEntity<IncidentAnalysisResult> publish(@Valid @RequestBody IncidentEvent event) {
        log.info("Received incident event: {}", event);

        producerService.publish(event);
        IncidentAnalysisResult analysis = resolutionService.analyze(event);

        log.info("Returning analyzed incident result to client: {}", analysis);
        return ResponseEntity.ok(analysis);
    }
}
