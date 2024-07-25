package com.triphippie.ollamaChatbotService.configuration;

import com.triphippie.ollamaChatbotService.service.Assistant;
import com.triphippie.ollamaChatbotService.vision.OllamaVisionLanguageModel;
import com.triphippie.ollamaChatbotService.vision.VisionLanguageModel;
import dev.langchain4j.data.embedding.Embedding;
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
import dev.langchain4j.rag.query.transformer.CompressingQueryTransformer;
import dev.langchain4j.rag.query.transformer.QueryTransformer;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;

import java.io.File;
import java.time.Duration;
import java.util.Collection;
import java.util.List;

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

    @Value("${langchain4j.compression-model.model-name}")
    private String compressionModelName;

    private static final String dbPath = "src/main/resources/static/embedding.store";

    @Bean
    public ChatLanguageModel model() {
        return OllamaChatModel.builder()
                .modelName(modelName)
                .baseUrl(url)
                .timeout(Duration.ofMinutes(15))
                .build();
    }

    @Bean
    public VisionLanguageModel visionModel() {
        return new OllamaVisionLanguageModel(
                url,
                visionModelName
        );
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
                .maxResults(3)
                .minScore(0.6)
                .build();
    }

    @Bean
    public QueryRouter queryRouter() {
        return new QueryRouter() {
            private final PromptTemplate PROMPT_TEMPLATE = PromptTemplate.from(
                    """
                            Do you have enough info to reply to the following query with a complete answer? 
                            Answer ONLY with 'yes' or 'no'. 
                            Query: '{{it}}'.
                            """
            );

            @Override
            public Collection<ContentRetriever> route(Query query) {
                Prompt prompt = PROMPT_TEMPLATE.apply(query.text());
                System.out.println(prompt.text());

                String aiMessage = model().generate(prompt.text());
                System.out.println("LLM decided: " + aiMessage);

                if (aiMessage.toLowerCase().contains("no")) {

                    // Printing out score for testing
                    Embedding queryEmbedding = embeddingModel().embed(query.text()).content();
                    List<EmbeddingMatch<TextSegment>> relevant = embeddingStore().findRelevant(queryEmbedding, 3);

                    EmbeddingMatch<TextSegment> embeddingMatch1 = relevant.get(0);
                    System.out.println(embeddingMatch1.score());
                    System.out.println(embeddingMatch1.embedded().text());

                    EmbeddingMatch<TextSegment> embeddingMatch2 = relevant.get(1);
                    System.out.println(embeddingMatch2.score());
                    System.out.println(embeddingMatch2.embedded().text());

                    EmbeddingMatch<TextSegment> embeddingMatch3 = relevant.get(2);
                    System.out.println(embeddingMatch3.score());
                    System.out.println(embeddingMatch3.embedded().text());
                    //

                    return singletonList(contentRetriever());
                }

                return emptyList();
            }
        };
    }

    @Bean
    public QueryTransformer queryTransformer() {
        ChatLanguageModel compressingLanguageModel = OllamaChatModel.builder()
                .baseUrl(url)
                .modelName(compressionModelName)
                .build();

        return new CompressingQueryTransformer(compressingLanguageModel);
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
