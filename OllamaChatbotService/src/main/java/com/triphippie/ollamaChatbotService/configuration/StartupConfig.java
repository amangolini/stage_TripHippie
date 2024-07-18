package com.triphippie.ollamaChatbotService.configuration;

import jakarta.annotation.PostConstruct;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.document.DocumentTransformer;
import org.springframework.ai.document.DocumentWriter;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StartupConfig {
    private final VectorStore vectorStore;

    @Autowired
    public StartupConfig(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @PostConstruct
    private void initializeEmbeddingStore() {
        // ETL Pipeline
        DocumentReader documentReader = new TextReader("static/documents/torri.txt");
        DocumentTransformer documentTransformer = new TokenTextSplitter();
        DocumentWriter documentWriter = vectorStore;

        documentWriter.accept(documentTransformer.apply(documentReader.get()));
    }
}
