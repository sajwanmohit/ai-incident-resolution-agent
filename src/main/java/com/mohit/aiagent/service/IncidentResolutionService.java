package com.mohit.aiagent.service;

import com.mohit.aiagent.model.IncidentEvent;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class IncidentResolutionService {

    private static final Logger log = LoggerFactory.getLogger(IncidentResolutionService.class);

    private final IncidentAnalyzerClient analyzerClient;

    public IncidentResolutionService(IncidentAnalyzerClient analyzerClient) {
        this.analyzerClient = analyzerClient;
    }

    public IncidentAnalysisResult analyze(IncidentEvent event) {
        log.info("Starting incident resolution for event: {}", event);
        IncidentAnalysisResult analysis = analyzerClient.analyze(event);
        log.info("Completed incident resolution for {}: summary={}, severity={}, confidence={}",
                event == null ? "unknown" : event.incidentId(),
                analysis.summary(),
                analysis.severity(),
                analysis.confidence());
        return analysis;
    }
}