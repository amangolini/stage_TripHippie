package com.triphippie.llmService.configuration;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LLMConfig {
    @Value("${ollama-api-url}")
    private String ollamaApiUrl;

    @Bean
    public OllamaChatModel model() {
        return new OllamaChatModel(
                new OllamaApi(ollamaApiUrl),
                OllamaOptions.create()
                        .withModel("llama2:7b")
                        .withTemperature(0.7f)
        );
    }

    @Bean
    public ChatClient.Builder client() {
        return ChatClient.builder(model());
    }

    @Bean
    public ChatMemory chatMemory() {
        return new InMemoryChatMemory();
    }
}
