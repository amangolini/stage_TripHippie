package com.triphippie.chatbotService.service;

import com.triphippie.chatbotService.model.Destination;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface Assistant {
    @SystemMessage(
            """
                    You're a helpful tour guide that provides information about the tourist attractions and culture
                    of the places you're asked about. Respond in a warm manner, suggesting possible activities to
                    do while on vacation at the specified place. If the question does not pertain the tourism sector,
                    say that you can't answer and explain why.
                    The response must have a maximum of 100 words.
                    """
    )
    String chat(@MemoryId int memoryId, @UserMessage String userMessage);

    @SystemMessage(
            """
                    Extract information about the tourist destination mentioned in {{dest}}.
                    Provide the answer in JSON format.
                    """
    )
    Destination summarizeDestination(@V(value = "dest") String destination);
}
