package com.mohit.aiagent.service;

import com.mohit.aiagent.model.IncidentEvent;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class IncidentAnalysisService implements IncidentAnalyzerClient {

    @Override
    public IncidentAnalysisResult analyze(IncidentEvent event) {
        String service = event.service() == null ? "unknown-service" : event.service().trim();
        String severity = event.severity() == null ? "LOW" : event.severity().trim().toUpperCase();
        String message = event.message() == null ? "" : event.message().toLowerCase();

        List<String> actions = new ArrayList<>();
        StringBuilder summaryBuilder = new StringBuilder();

        if (message.contains("timeout") || message.contains("latency") || message.contains("slow")) {
            actions.add("Check downstream dependency latency and timeouts.");
            summaryBuilder.append("The incident appears tied to service latency or a downstream timeout.");
        }

        if (message.contains("error") || message.contains("exception") || message.contains("failure") || message.contains("fail")) {
            actions.add("Review recent application logs and stack traces for root-cause patterns.");
            if (summaryBuilder.isEmpty()) {
                summaryBuilder.append("The incident indicates an application or dependency failure.");
            }
        }

        if (service.contains("db") || service.contains("database") || service.contains("sql")) {
            actions.add("Verify database connection health, query performance, and pool saturation.");
        }

        if (service.contains("api") || service.contains("gateway") || service.contains("payment") || service.contains("auth")) {
            actions.add("Inspect API gateway and upstream dependency availability for traffic spikes or failures.");
        }

        if (service.contains("cache") || service.contains("redis") || service.contains("queue")) {
            actions.add("Validate cache or queue health and message backlog before escalating.");
        }

        if (actions.isEmpty()) {
            actions.add("Check service health, recent deploys, and dependencies for the first failing signal.");
        }

        if (summaryBuilder.isEmpty()) {
            summaryBuilder.append("AI analysis indicates a ").append(severity.toLowerCase()).append(" severity issue in ").append(service).append(".");
        }

        double confidence = switch (severity) {
            case "CRITICAL" -> 0.96;
            case "HIGH" -> 0.89;
            case "MEDIUM" -> 0.78;
            default -> 0.68;
        };

        String summary = summaryBuilder.toString() + " Service: " + service + ".";
        return new IncidentAnalysisResult(severity, confidence, summary, actions);
    }
}
