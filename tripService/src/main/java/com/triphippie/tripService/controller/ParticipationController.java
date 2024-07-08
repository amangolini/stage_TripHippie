package com.triphippie.tripService.controller;

import com.triphippie.tripService.model.participation.ParticipationDto;
import com.triphippie.tripService.service.ParticipationService;
import com.triphippie.tripService.service.TripServiceException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("api/participation")
public class ParticipationController {
    private final ParticipationService participationService;

    @Autowired
    public ParticipationController(ParticipationService participationService) {
        this.participationService = participationService;
    }

    @PostMapping()
    public ResponseEntity<?> postParticipation(
            @RequestBody @Valid ParticipationDto inDto
    ) {
        try {
            participationService.createParticipation(inDto);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (TripServiceException e) {
            switch (e.getError()) {
                case NOT_FOUND -> throw new ResponseStatusException(HttpStatus.NOT_FOUND);
                case FORBIDDEN -> throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Write access forbidden");
                default -> throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @GetMapping()
    public ResponseEntity<?> getJourneys(@RequestParam("tripId") Long id) {
        try {
            return new ResponseEntity<>(participationService.findParticipation(id), HttpStatus.OK);
        } catch (TripServiceException e) {
            switch (e.getError()) {
                case NOT_FOUND -> throw new ResponseStatusException(HttpStatus.NOT_FOUND);
                default -> throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @DeleteMapping()
    public ResponseEntity<?> deleteJourney(
            @RequestBody @Valid ParticipationDto inDto
    ) {
        try {
            participationService.deleteParticipation(inDto);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (TripServiceException e) {
            switch (e.getError()) {
                case FORBIDDEN -> throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Write access forbidden");
                default -> throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }
}
