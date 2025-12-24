package com.guilinares.clinicai.ai.clients;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guilinares.clinicai.ai.dto.OpenAiChatRequest;
import com.guilinares.clinicai.ai.dto.OpenAiChatResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;

@Component
public class OpenAiClient {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final String model;

    public OpenAiClient(
            WebClient openAiWebClient,
            ObjectMapper objectMapper,
            @Value("${app.ai.openai.model}") String model
    ) {
        this.webClient = openAiWebClient;
        this.objectMapper = objectMapper;
        this.model = model;
    }

    public JsonNode chatAsJson(String systemPrompt, String userPrompt) {

        OpenAiChatRequest request = new OpenAiChatRequest(
                model,
                List.of(
                        new OpenAiChatRequest.Message("system", systemPrompt),
                        new OpenAiChatRequest.Message("user", userPrompt)
                )
        );

        OpenAiChatResponse response = webClient.post()
                .uri("/chat/completions")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(OpenAiChatResponse.class)
                .onErrorResume(ex -> {
                    ex.printStackTrace();
                    return Mono.empty();
                })
                .block();

        if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
            return objectMapper.createObjectNode();
        }

        String content = response.getChoices().get(0).getMessage().getContent();
        try {
            return objectMapper.readTree(content);
        } catch (IOException e) {
            e.printStackTrace();
            return objectMapper.createObjectNode();
        }
    }
}
