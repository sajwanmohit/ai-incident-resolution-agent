package com.mohit.aiagent.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mohit.aiagent.model.IncidentEvent;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.prompt.Prompt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OpenAiIncidentAnalyzerTest {

    @Test
    void usesLiveChatModelResponseForIncidentAnalysis() {
        ChatModel chatModel = mock(ChatModel.class);
        ChatResponse chatResponse = mock(ChatResponse.class);
        Generation generation = mock(Generation.class);
        AssistantMessage assistantMessage = mock(AssistantMessage.class);
        when(chatModel.call(any(Prompt.class))).thenReturn(chatResponse);
        when(chatResponse.getResult()).thenReturn(generation);
        when(generation.getOutput()).thenReturn(assistantMessage);
        when(assistantMessage.getText()).thenReturn("""
                {"severity":"HIGH","confidence":0.91,"summary":"Payment API timeout","rootCause":"Upstream payment API latency caused the timeout.","evidence":["Incident severity is HIGH","Service is payments","Message indicates timeout"],"recommendedActions":["Check payment provider latency","Verify upstream dependency health","Review recent deployment and alert history"]}
                """);

        OpenAiIncidentAnalyzer analyzer = new OpenAiIncidentAnalyzer(
                chatModel,
                new ObjectMapper()
        );

        IncidentAnalysisResult result = analyzer.analyze(
                new IncidentEvent("INC-42", "payments", "HIGH", "Payment API timeout")
        );

        assertEquals("HIGH", result.severity());
        assertEquals("Payment API timeout", result.summary());
        assertEquals("Check payment provider latency", result.recommendedActions().getFirst());
        verify(chatModel).call(any(Prompt.class));
    }
}