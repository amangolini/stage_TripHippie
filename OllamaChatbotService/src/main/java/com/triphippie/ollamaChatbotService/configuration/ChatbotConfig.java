package com.triphippie.ollamaChatbotService.configuration;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatbotConfig {
    @Value("${ollama-api-url}")
    private String ollamaApiUrl;

    @Bean
    public OllamaApi ollamaApi() { return new OllamaApi(ollamaApiUrl); }

    @Bean
    public OllamaChatModel model() {
        return new OllamaChatModel(
                ollamaApi(),
                OllamaOptions.create()
                        .withModel("mistral:7b-instruct-q2_K")
                        .withTemperature(0.7f)
        );
    }

    @Bean
    public ChatClient.Builder client() {
        return ChatClient.builder(model())
                .defaultAdvisors(new QuestionAnswerAdvisor(vectorStore(), SearchRequest.defaults()));
    }

    @Bean
    public ChatMemory chatMemory() {
        return new InMemoryChatMemory();
    }

    @Bean
    public EmbeddingModel embeddingModel() { return new OllamaEmbeddingModel(ollamaApi()); }

    @Bean
    public VectorStore vectorStore() { return new SimpleVectorStore(embeddingModel()); }
}
