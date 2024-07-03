package com.triphippie.chatbotService.controller;

import com.triphippie.chatbotService.model.Destination;
import com.triphippie.chatbotService.model.Query;
import com.triphippie.chatbotService.model.Result;
import com.triphippie.chatbotService.service.ChatbotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequestMapping("api/chatbot")
public class ChatbotController {
    private ChatbotService service;

    @Autowired
    public ChatbotController(ChatbotService service) {
        this.service = service;
    }

    @PostMapping("/query")
    public ResponseEntity<?> postQuery(
            @RequestHeader("auth-user-id") Integer authUser,
            @RequestBody Query query
    ) {
        Optional<Result> result = service.ask(query, authUser);
        if(result.isPresent()) return new ResponseEntity<>(result, HttpStatus.OK);
        else throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
    }

    @PostMapping("/summarize")
    public ResponseEntity<?> postSummarize(
            @RequestHeader("auth-user-id") Integer authUser,
            @RequestBody Query query
    ) {
        Optional<Destination> result = service.summarizeDestination(query);
        if(result.isPresent()) return new ResponseEntity<>(result, HttpStatus.OK);
        else throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
    }

}
