package com.triphippie.ollamaChatbotService.controller;

import com.triphippie.ollamaChatbotService.model.Query;
import com.triphippie.ollamaChatbotService.model.Result;
import com.triphippie.ollamaChatbotService.service.ChatbotService;
import com.triphippie.ollamaChatbotService.service.ChatbotServiceException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
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
    @PostMapping("/documents")
    public ResponseEntity<?> postDocument(@RequestParam("file") MultipartFile file) {
        try {
            return new ResponseEntity<>(service.uploadDocument(file), HttpStatus.CREATED);
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

    @GetMapping(value = "/documents/{docId}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> getDocument(@PathVariable(name = "docId") Long docId) {
        try {
            return new ResponseEntity<>(service.getDocument(docId), HttpStatus.OK);
        } catch (ChatbotServiceException ex) {
            switch (ex.getError()) {
                case FORBIDDEN -> throw new ResponseStatusException(HttpStatus.FORBIDDEN);
                case NOT_FOUND -> throw new ResponseStatusException(HttpStatus.NOT_FOUND);
                default -> throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @DeleteMapping("/documents/{docId}")
    public ResponseEntity<?> deleteDocument(@PathVariable(name = "docId") Long docId) {
        try {
            service.deleteDocument(docId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (ChatbotServiceException ex) {
            switch (ex.getError()) {
                case FORBIDDEN -> throw new ResponseStatusException(HttpStatus.FORBIDDEN);
                default -> throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    // VISION
    @PostMapping("/guessDestination")
    public ResponseEntity<?> guessDestination(
            @RequestParam("file") MultipartFile picture
    ) {
        try {
            Optional<Result> result = service.spotDestination(picture);
            if(result.isPresent()) return new ResponseEntity<>(result, HttpStatus.OK);
            else throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
