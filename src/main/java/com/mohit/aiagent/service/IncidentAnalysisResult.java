package com.mohit.aiagent.service;

import java.util.List;

public record IncidentAnalysisResult(
        String severity,
        double confidence,
        String summary,
        List<String> recommendedActions,
        String rootCause,
        List<String> evidence
) {
    public IncidentAnalysisResult(String severity, double confidence, String summary, List<String> recommendedActions) {
        this(severity, confidence, summary, recommendedActions, "", List.of());
    }
}
