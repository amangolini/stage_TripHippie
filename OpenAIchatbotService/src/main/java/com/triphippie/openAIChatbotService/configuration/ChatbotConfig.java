package com.triphippie.openAIChatbotService.configuration;

import com.triphippie.openAIChatbotService.service.Assistant;
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
import dev.langchain4j.rag.query.transformer.CompressingQueryTransformer;
import dev.langchain4j.rag.query.transformer.QueryTransformer;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;

import java.io.File;
import java.util.Collection;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

@Configuration
public class ChatbotConfig {
    @Value("${langchain4j.open-ai.chat-model.api-key}")
    private String apiKey;

    @Value("${langchain4j.open-ai.chat-model.model-name}")
    private String modelName;

    private static final String dbPath = "src/main/resources/static/embedding.store";

    @Bean
    public ChatLanguageModel model() {
        return OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .build();
    }

    @Bean
    public EmbeddingStore<TextSegment> embeddingStore() {
        if((new File(dbPath)).isFile()) return InMemoryEmbeddingStore.fromFile(dbPath);
        else return new InMemoryEmbeddingStore<>();
    }

    @EventListener(ContextClosedEvent.class)
    private void persistEmbeddingStore() {
        InMemoryEmbeddingStore<TextSegment> db = (InMemoryEmbeddingStore<TextSegment>)embeddingStore();
        db.serializeToFile(dbPath);
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
                .maxResults(3)
                .minScore(0.6)
                .build();
    }

    @Bean
    public QueryRouter queryRouter() {
        return new QueryRouter() {

            private final PromptTemplate PROMPT_TEMPLATE = PromptTemplate.from(
                    """
                            Do you have enough knowledge to elaborate the following query further? 
                            Answer ONLY with 'yes' or 'no'.
                            Query: '{{it}}'
                            """
            );

            @Override
            public Collection<ContentRetriever> route(Query query) {
                Prompt prompt = PROMPT_TEMPLATE.apply(query.text());

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
    public QueryTransformer queryTransformer() {
        return new CompressingQueryTransformer(model());
    }

    @Bean
    public RetrievalAugmentor retrievalAugmentor() {
        return DefaultRetrievalAugmentor.builder()
                .queryRouter(queryRouter())
                .queryTransformer(queryTransformer())
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
