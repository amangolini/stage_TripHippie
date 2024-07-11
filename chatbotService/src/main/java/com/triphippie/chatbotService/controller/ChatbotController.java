package com.triphippie.chatbotService.controller;

import com.triphippie.chatbotService.model.Query;
import com.triphippie.chatbotService.model.Result;
import com.triphippie.chatbotService.service.ChatbotService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequestMapping("api/chatbot")
public class ChatbotController {
    private final ChatbotService service;

    @Autowired
    public ChatbotController(ChatbotService service) {
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
}
