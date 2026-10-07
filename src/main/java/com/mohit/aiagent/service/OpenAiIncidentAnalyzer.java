package com.mohit.aiagent.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mohit.aiagent.model.IncidentEvent;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
@ConditionalOnExpression("'${spring.ai.openai.api-key:}' != ''")
public class OpenAiIncidentAnalyzer implements IncidentAnalyzerClient {

    private final ChatModel chatModel;
    private final IncidentAnalysisService fallbackAnalyzer;
    private final ObjectMapper objectMapper;

    public OpenAiIncidentAnalyzer(ChatModel chatModel, IncidentAnalysisService fallbackAnalyzer) {
        this.chatModel = chatModel;
        this.fallbackAnalyzer = fallbackAnalyzer;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public IncidentAnalysisResult analyze(IncidentEvent event) {
        try {
            String prompt = """
                    Analyze this production incident and respond with valid JSON only.
                    Required JSON fields:
                    - severity: string
                    - confidence: number
                    - summary: string
                    - recommendedActions: array of strings

                    Incident event:
                    incidentId: %s
                    service: %s
                    severity: %s
                    message: %s

                    Return only JSON, no markdown fences.
                    """.formatted(
                    event.incidentId(),
                    event.service(),
                    event.severity(),
                    event.message()
            );

            String response = chatModel.call(new Prompt(prompt)).getResult().getOutput().getText();
            String normalized = response.replace("```json", "").replace("```", "").trim();
            return objectMapper.readValue(normalized, IncidentAnalysisResult.class);
        } catch (Exception ex) {
            return fallbackAnalyzer.analyze(event);
        }
    }
}
