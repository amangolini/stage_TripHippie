package com.triphippie.ollamaChatbotService.service;

import com.triphippie.ollamaChatbotService.model.Destination;
import com.triphippie.ollamaChatbotService.model.Query;
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
        Destination info = assistant.summarizeDestination(destination.query());
        return Optional.of(info);
    }
}
