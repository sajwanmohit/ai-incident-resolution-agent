package com.mohit.aiagent.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Service
public class LocalLlmManager {

    private static final Logger log = LoggerFactory.getLogger(LocalLlmManager.class);
    public static final String DEFAULT_MODEL_NAME = "ai/qwen3";

    public boolean stopModel(String modelName) {
        String effectiveModelName = modelName == null || modelName.isBlank() ? DEFAULT_MODEL_NAME : modelName.trim();
        log.info("Stopping local LLM model via Docker CLI: {}", effectiveModelName);

        try {
            Process process = new ProcessBuilder("docker", "model", "stop", effectiveModelName)
                    .redirectErrorStream(true)
                    .start();

            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append(System.lineSeparator());
                }
            }

            int exitCode = process.waitFor();
            log.info("docker model stop {} exited with code {}. Output: {}",
                    effectiveModelName, exitCode, output);
            return exitCode == 0;
        } catch (Exception ex) {
            log.error("Failed to stop local LLM model {} via Docker CLI", effectiveModelName, ex);
            return false;
        }
    }
}

