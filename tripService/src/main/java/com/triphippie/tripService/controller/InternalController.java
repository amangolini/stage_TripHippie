package com.triphippie.tripService.controller;

import com.triphippie.tripService.service.InternalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("api/internal/trips")
public class InternalController {
    private final InternalService internalService;

    @Autowired
    public InternalController(InternalService internalService) {
        this.internalService = internalService;
    }

    @PostMapping("/deletedUser/{userId}")
    public ResponseEntity<?> deleteTripsByUser(
            @PathVariable("userId") Integer id
    ) {
        internalService.deleteTripsByUserId(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
