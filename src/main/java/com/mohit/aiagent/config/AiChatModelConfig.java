package com.mohit.aiagent.config;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiChatModelConfig {

    @Bean
    @ConditionalOnProperty(name = "spring.ai.ollama.chat.options.model", matchIfMissing = false)
    public ChatModel ollamaChatModel(
            @Value("${spring.ai.ollama.base-url:http://localhost:11434}") String baseUrl,
            @Value("${spring.ai.ollama.chat.options.model}") String model
    ) {
        return OllamaChatModel.builder()
                .ollamaApi(new OllamaApi(baseUrl))
                .defaultOptions(OllamaOptions.builder().model(model).build())
                .build();
    }

    @Bean
    @ConditionalOnExpression("'${spring.ai.openai.api-key:}' != ''")
    public ChatModel openAiChatModel(
            @Value("${spring.ai.openai.api-key}") String apiKey,
            @Value("${spring.ai.openai.base-url:https://api.openai.com/v1}") String baseUrl,
            @Value("${spring.ai.openai.chat.options.model:gpt-4o-mini}") String model
    ) {
        return OpenAiChatModel.builder()
                .openAiApi(OpenAiApi.builder()
                        .apiKey(apiKey)
                        .baseUrl(baseUrl)
                        .build())
                .defaultOptions(OpenAiChatOptions.builder().model(model).build())
                .build();
    }
}
