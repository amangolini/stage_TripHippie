package com.triphippie.llmService.controller;

import com.triphippie.llmService.model.Query;
import com.triphippie.llmService.model.Result;
import com.triphippie.llmService.service.LLMService;
import jakarta.validation.Valid;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;

import java.util.Optional;

@RestController
@RequestMapping("api/chatbot")
public class LLMController {
    private final LLMService service;

    @Autowired
    public LLMController(LLMService service) {
        this.service = service;
    }

    @PostMapping("/query")
    public ResponseEntity<?> postQuery(
            @RequestBody @Valid Query query
    ) {
        Optional<Result> result = service.ask(query);
        if(result.isPresent()) return new ResponseEntity<>(result, HttpStatus.OK);
        else throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
    }
/*
    @PostMapping("/stream")
    public Flux<ChatResponse> generateStream(
            @RequestBody @Valid Query query
    ) {
        return service.stream(query);
    }*/
}
