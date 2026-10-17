package com.mohit.aiagent.controller;

import com.mohit.aiagent.service.LocalLlmManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class LlmAdminController {

    private static final Logger log = LoggerFactory.getLogger(LlmAdminController.class);

    private final LocalLlmManager localLlmManager;

    public LlmAdminController(LocalLlmManager localLlmManager) {
        this.localLlmManager = localLlmManager;
    }

    @PostMapping("/llm/stop")
    public ResponseEntity<String> stopLlm() {
        log.info("Admin request received to stop local LLM model");
        boolean stopped = localLlmManager.stopModel(LocalLlmManager.DEFAULT_MODEL_NAME);

        if (stopped) {
            return ResponseEntity.ok("Local LLM model stopped successfully: " + LocalLlmManager.DEFAULT_MODEL_NAME);
        }

        return ResponseEntity.status(500).body("Failed to stop local LLM model: " + LocalLlmManager.DEFAULT_MODEL_NAME);
    }
}

