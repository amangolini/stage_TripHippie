package com.triphippie.openAIChatbotService.service;

import com.triphippie.openAIChatbotService.model.Destination;
import com.triphippie.openAIChatbotService.model.Query;
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
