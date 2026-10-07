package com.mohit.aiagent.service;

import com.mohit.aiagent.model.IncidentEvent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IncidentAnalysisServiceTest {

    @Test
    void producesAiStyleAnalysisForHighSeverityIncident() {
        IncidentAnalysisService service = new IncidentAnalysisService();
        IncidentEvent event = new IncidentEvent("INC-42", "payments", "HIGH", "Payment API timeout while processing card requests");

        IncidentAnalysisResult result = service.analyze(event);

        assertEquals("HIGH", result.severity());
        assertTrue(result.confidence() > 0.5);
        assertTrue(result.summary().toLowerCase().contains("payments"));
        assertTrue(result.recommendedActions().stream().anyMatch(action -> action.toLowerCase().contains("timeout") || action.toLowerCase().contains("dependency")));
    }
}
