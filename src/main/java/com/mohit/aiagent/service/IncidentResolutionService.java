package com.mohit.aiagent.service;

import com.mohit.aiagent.model.IncidentEvent;
import org.springframework.stereotype.Service;

@Service
public class IncidentResolutionService {

    private final IncidentAnalyzerClient analyzerClient;

    public IncidentResolutionService(IncidentAnalyzerClient analyzerClient) {
        this.analyzerClient = analyzerClient;
    }

    public String analyze(IncidentEvent event) {
        IncidentAnalysisResult analysis = analyzerClient.analyze(event);
        return "AI analysis: " + analysis.summary()
                + " Confidence: " + analysis.confidence()
                + " Recommended actions: " + String.join(" | ", analysis.recommendedActions());
    }
}