package com.triphippie.tripService.controller;

import com.triphippie.tripService.model.journey.JourneyInDto;
import com.triphippie.tripService.model.journey.JourneyUpdate;
import com.triphippie.tripService.service.JourneyService;
import com.triphippie.tripService.service.TripServiceException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("api/journeys")
public class JourneyController {
    private final JourneyService journeyService;

    @Autowired
    public JourneyController(JourneyService journeyService) {
        this.journeyService = journeyService;
    }

    @PostMapping()
    public ResponseEntity<?> postJourney(
            @RequestBody @Valid JourneyInDto journeyInDto
    ) {
        try {
            journeyService.createJourney(journeyInDto);
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
            return new ResponseEntity<>(journeyService.findJourneys(id), HttpStatus.OK);
        } catch (TripServiceException e) {
            switch (e.getError()) {
                case NOT_FOUND -> throw new ResponseStatusException(HttpStatus.NOT_FOUND);
                default -> throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @GetMapping("/{journeyId}")
    public ResponseEntity<?> getJourney(@PathVariable("journeyId") Long id) {
        try {
            return new ResponseEntity<>(journeyService.findJourneyById(id), HttpStatus.OK);
        } catch (TripServiceException e) {
            switch (e.getError()) {
                case NOT_FOUND -> throw new ResponseStatusException(HttpStatus.NOT_FOUND);
                default -> throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @PutMapping("/{journeyId}")
    public ResponseEntity<?> putJourney(
            @PathVariable("journeyId") Long id,
            @RequestBody @Valid JourneyUpdate update
    ) {
        try {
            journeyService.modifyJourney(id, update);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (TripServiceException e) {
            switch (e.getError()) {
                case NOT_FOUND -> throw new ResponseStatusException(HttpStatus.NOT_FOUND);
                case FORBIDDEN -> throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Write access forbidden");
                default -> throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @DeleteMapping("/{journeyId}")
    public ResponseEntity<?> deleteJourney(
            @PathVariable("journeyId") Long id
    ) {
        try {
            journeyService.deleteJourney(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (TripServiceException e) {
            switch (e.getError()) {
                case FORBIDDEN -> throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Write access forbidden");
                default -> throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }
}
