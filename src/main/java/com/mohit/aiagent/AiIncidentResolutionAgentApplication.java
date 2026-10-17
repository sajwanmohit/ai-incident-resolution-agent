package com.mohit.aiagent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
public class AiIncidentResolutionAgentApplication {
    private static final Logger log = LoggerFactory.getLogger(AiIncidentResolutionAgentApplication.class);

    public static void main(String[] args) {
        log.info("Starting AI Incident Resolution Agent with arguments: {}", (Object[]) args);
        SpringApplication.run(AiIncidentResolutionAgentApplication.class, args);
    }
}
