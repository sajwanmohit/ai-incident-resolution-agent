package com.mohit.aiagent.config;

import com.mohit.aiagent.service.LocalLlmManager;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LocalLlmShutdownHook {

    private static final Logger log = LoggerFactory.getLogger(LocalLlmShutdownHook.class);

    private final LocalLlmManager localLlmManager;

    public LocalLlmShutdownHook(LocalLlmManager localLlmManager) {
        this.localLlmManager = localLlmManager;
    }

    @PreDestroy
    public void shutdown() {
        log.info("Application shutdown detected; stopping local LLM model if it is running");
        localLlmManager.stopModel(LocalLlmManager.DEFAULT_MODEL_NAME);
    }
}

