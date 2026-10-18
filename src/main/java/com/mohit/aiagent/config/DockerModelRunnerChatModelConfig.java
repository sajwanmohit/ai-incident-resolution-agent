package com.mohit.aiagent.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DockerModelRunnerChatModelConfig {

    private static final Logger log = LoggerFactory.getLogger(DockerModelRunnerChatModelConfig.class);

    @Bean
    @ConditionalOnMissingBean(OpenAiChatModel.class)
    public OpenAiChatModel dockerModelRunnerChatModel(
            @Value("${spring.ai.openai.api-key:docker-model-runner}") String apiKey,
            @Value("${spring.ai.openai.base-url:http://localhost:12434/engines/v1}") String baseUrl,
            @Value("${spring.ai.openai.chat.completions-path:/chat/completions}") String completionsPath,
            @Value("${spring.ai.openai.chat.options.model:docker.io/ai/qwen3:latest}") String model,
            @Value("${spring.ai.openai.chat.options.temperature:0.2}") double temperature
    ) {
        log.info("Initializing local Docker Model Runner OpenAI-compatible client: baseUrl={}, completionsPath={}, model={}, temperature={}",
                baseUrl, completionsPath, model, temperature);
        return OpenAiChatModel.builder()
                .openAiApi(OpenAiApi.builder()
                        .apiKey(apiKey)
                        .baseUrl(baseUrl)
                        .completionsPath(completionsPath)
                        .build())
                .defaultOptions(OpenAiChatOptions.builder()
                        .model(model)
                        .temperature(temperature)
                        .build())
                .build();
    }
}
