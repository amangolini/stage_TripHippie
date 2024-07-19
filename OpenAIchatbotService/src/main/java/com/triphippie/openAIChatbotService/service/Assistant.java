package com.triphippie.openAIChatbotService.service;

import com.triphippie.openAIChatbotService.model.Destination;
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
                    While answering, DO NOT say whether you're referring to the provided info or not.
                    """
    )
    String chat(@MemoryId int memoryId, @UserMessage String userMessage);

    @SystemMessage(
            """
                    IF {{dest}} is a tourist destination, extract information about it in JSON format.
                    Otherwise do nothing.
                    """
    )
    Destination summarizeDestination(@V(value = "dest") String destination);
}
