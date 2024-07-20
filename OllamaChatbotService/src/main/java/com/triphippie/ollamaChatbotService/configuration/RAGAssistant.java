package com.triphippie.ollamaChatbotService.configuration;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.UserMessage;

public interface RAGAssistant {
    String chat(@MemoryId int memoryId, @UserMessage String userMessage);
}