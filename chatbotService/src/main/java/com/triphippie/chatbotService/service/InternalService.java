package com.triphippie.chatbotService.service;

import com.triphippie.chatbotService.model.Destination;
import com.triphippie.chatbotService.model.Query;
import dev.ai4j.openai4j.OpenAiHttpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class InternalService {
    private final Assistant assistant;

    @Autowired
    public InternalService(Assistant assistant) {
        this.assistant = assistant;
    }

    public Optional<Destination> summarizeDestination(Query destination) {
        try {
            Destination info = assistant.summarizeDestination(destination.query());
            return Optional.of(info);

        } catch (OpenAiHttpException e) {
            return Optional.empty();
        }
    }
}
