package com.triphippie.ollamaChatbotService.service;

import com.triphippie.ollamaChatbotService.model.Destination;
import com.triphippie.ollamaChatbotService.model.Query;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class InternalService {
    private final ChatClient chatClient;

    @Autowired
    public InternalService(ChatClient.Builder builder, ChatMemory chatMemory) {
        this.chatClient = builder.build();
    }

    public Optional<Destination> summarizeDestination(Query query) {
        try {
            Destination destination = this.chatClient.prompt()
                    .user(u ->
                            u.text("""
                                        Generate information about {dest}
                                        """
                                    )
                                    .param("dest", query.query()))
                    .call()
                    .entity(Destination.class);

            return Optional.of(destination);
        } catch (RuntimeException ex) {
            return Optional.empty();
        }
    }
}
