package com.mohit.aiagent.service;

import com.mohit.aiagent.model.IncidentEvent;

public interface IncidentAnalyzerClient {
    IncidentAnalysisResult analyze(IncidentEvent event);
}
