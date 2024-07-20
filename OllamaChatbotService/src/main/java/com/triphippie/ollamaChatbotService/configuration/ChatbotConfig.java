package com.triphippie.ollamaChatbotService.configuration;

import com.triphippie.ollamaChatbotService.model.Conversation;
import com.triphippie.ollamaChatbotService.repository.ConversationRepository;
import com.triphippie.ollamaChatbotService.security.PrincipalFacade;
import com.triphippie.ollamaChatbotService.service.Assistant;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.rag.query.router.QueryRouter;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

@Configuration
public class ChatbotConfig {
    @Value("${langchain4j.chat-model.model-url}")
    private String url;

    @Value("${langchain4j.chat-model.model-name}")
    private String modelName;

    @Value("${langchain4j.embedding-model.model-name}")
    private String embeddingModelName;

    @Value("${langchain4j.vision-model.model-name}")
    private String visionModelName;

    @Bean
    public ChatLanguageModel model() {
        return OllamaChatModel.builder()
                .modelName(modelName)
                .baseUrl(url)
                .timeout(Duration.ofMinutes(8))
                .build();
    }

    @Bean
    public EmbeddingStore<TextSegment> embeddingStore() {
        return new InMemoryEmbeddingStore<>();
    }

    @Bean
    public EmbeddingModel embeddingModel() {
        return OllamaEmbeddingModel.builder()
                .modelName(embeddingModelName)
                .baseUrl(url)
                .build();
    }

    @Bean
    public ContentRetriever contentRetriever() {
        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore())
                .embeddingModel(embeddingModel())
                .maxResults(2)
                .minScore(0.7)
                .build();
    }

    @Bean
    public QueryRouter queryRouter() {
        return new QueryRouter() {
            private final PromptTemplate PROMPT_TEMPLATE = PromptTemplate.from(
                    """
                            Do you have enough info to reply to the following query with a complete answer? 
                            Answer ONLY with 'yes' or 'no'. 
                            Query: '{{query}}'. Context: {{context}}.
                            """
            );

            @Autowired
            private UserContext userContext;

            @Override
            public Collection<ContentRetriever> route(Query query) {
                Prompt prompt = (userContext.getContext() != null)
                        ? PROMPT_TEMPLATE.apply(Map.of("query", query.text(), "context", userContext.getContext()))
                        : PROMPT_TEMPLATE.apply(Map.of("query", query.text(), "context", "none"));

                AiMessage aiMessage = model().generate(prompt.toUserMessage()).content();
                System.out.println("LLM decided: " + aiMessage.text());

                if (aiMessage.text().toLowerCase().contains("no")) {
                    return singletonList(contentRetriever());
                }

                return emptyList();
            }
        };
    }

    @Bean
    public RetrievalAugmentor retrievalAugmentor() {
        return DefaultRetrievalAugmentor.builder()
                .queryRouter(queryRouter())
                .build();
    }

    @Bean
    public Assistant assistant() {
        return AiServices.builder(Assistant.class)
                .chatLanguageModel(model())
                .chatMemoryProvider(id -> MessageWindowChatMemory.withMaxMessages(10))
                .retrievalAugmentor(retrievalAugmentor())
                .build();
    }


}
