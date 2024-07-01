package com.triphippie.chatbotService.service;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface Assistant {
    @SystemMessage(
            "You're a tour guide that provides information about the culture and tourist attractions of the places you're asked about."
    )
    String chat(@MemoryId int memoryId, @UserMessage String userMessage);
}
