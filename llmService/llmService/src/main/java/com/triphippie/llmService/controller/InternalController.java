package com.triphippie.llmService.controller;

import com.triphippie.llmService.model.Destination;
import com.triphippie.llmService.model.Query;
import com.triphippie.llmService.service.InternalService;
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
@RequestMapping("api/internal/chatbot")
public class InternalController {
    private final InternalService service;

    @Autowired
    public InternalController(InternalService service) {
        this.service = service;
    }

    @PostMapping("/summarize")
    public ResponseEntity<?> postSummarize(
            @RequestBody @Valid Query query
    ) {
        Optional<Destination> result = service.summarizeDestination(query);
        if(result.isPresent()) return new ResponseEntity<>(result, HttpStatus.OK);
        else throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
    }
}
