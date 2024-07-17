package com.triphippie.chatbotService.configuration;

import com.triphippie.chatbotService.service.Assistant;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.rag.query.router.QueryRouter;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

@Configuration
public class ChatbotConfig {
    @Value("${langchain4j.open-ai.chat-model.api-key}")
    private String apiKey;

    @Value("${langchain4j.open-ai.chat-model.model-name}")
    private String modelName;

    @Bean
    public ChatLanguageModel model() {
        return OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .build();
    }

    @Bean
    public EmbeddingStore<TextSegment> embeddingStore() {
        return new InMemoryEmbeddingStore<>();
    }

    @Bean
    public EmbeddingModel embeddingModel() {
        return OpenAiEmbeddingModel.withApiKey(apiKey);
    }

    @Bean
    public ContentRetriever contentRetriever() {
        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore())
                .embeddingModel(embeddingModel())
                .maxResults(2)
                .minScore(0.75)
                .build();
    }

    @Bean
    public QueryRouter queryRouter() {
        return new QueryRouter() {

            private final PromptTemplate PROMPT_TEMPLATE = PromptTemplate.from(
                    """
                            Do you have enough info to reply to the following query with a complete answer? 
                            Answer ONLY with 'yes', 'no' or 'maybe'.
                            Query: '{{it}}'
                            """
            );

            @Override
            public Collection<ContentRetriever> route(Query query) {

                Prompt prompt = PROMPT_TEMPLATE.apply(query.text());

                AiMessage aiMessage = model().generate(prompt.toUserMessage()).content();
                System.out.println("LLM decided: " + aiMessage.text());

                if (!aiMessage.text().toLowerCase().contains("no")) {
                    return emptyList();
                }

                return singletonList(contentRetriever());
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
