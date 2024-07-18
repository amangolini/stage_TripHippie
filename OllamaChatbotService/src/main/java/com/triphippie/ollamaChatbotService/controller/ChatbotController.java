package com.triphippie.ollamaChatbotService.controller;

import com.triphippie.ollamaChatbotService.model.Query;
import com.triphippie.ollamaChatbotService.model.Result;
import com.triphippie.ollamaChatbotService.service.ChatbotService;
import com.triphippie.ollamaChatbotService.service.ChatbotServiceException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
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

    // RAG
    @PostMapping("/upload")
    public ResponseEntity<?> postUpload(@RequestParam("file") MultipartFile file) {
        try {
            service.uploadDocument(file);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (ChatbotServiceException ex) {
            switch (ex.getError()) {
                case BAD_REQUEST -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
                case UNSUPPORTED -> throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
                default -> throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

/*
    @PostMapping("/stream")
    public Flux<ChatResponse> generateStream(
            @RequestBody @Valid Query query
    ) {
        return service.stream(query);
    }*/
}
