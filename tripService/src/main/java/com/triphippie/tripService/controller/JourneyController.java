package com.triphippie.tripService.controller;

import com.triphippie.tripService.model.JourneyInDto;
import com.triphippie.tripService.model.JourneyUpdate;
import com.triphippie.tripService.service.TripService;
import com.triphippie.tripService.service.TripServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Controller
@RequestMapping("api/journeys")
public class JourneyController {
    private TripService tripService;

    @Autowired
    public JourneyController(TripService tripService) {
        this.tripService = tripService;
    }

    @PostMapping()
    public ResponseEntity<?> postJourney(
            @RequestHeader("auth-user-id") Integer userId,
            @RequestBody JourneyInDto journeyInDto
    ) {
        try {
            tripService.createJourney(userId, journeyInDto);
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
            return new ResponseEntity<>(tripService.findJourneys(id), HttpStatus.OK);
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
            return new ResponseEntity<>(tripService.findJourneyById(id), HttpStatus.OK);
        } catch (TripServiceException e) {
            switch (e.getError()) {
                case NOT_FOUND -> throw new ResponseStatusException(HttpStatus.NOT_FOUND);
                default -> throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @PutMapping("/{journeyId}")
    public ResponseEntity<?> putJourney(
            @RequestHeader("auth-user-id") Integer userId,
            @PathVariable("journeyId") Long id,
            @RequestBody JourneyUpdate update
    ) {
        try {
            tripService.modifyJourney(userId, id, update);
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
            @RequestHeader("auth-user-id") Integer userId,
            @PathVariable("journeyId") Long id
    ) {
        try {
            tripService.deleteJourney(userId, id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (TripServiceException e) {
            switch (e.getError()) {
                case FORBIDDEN -> throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Write access forbidden");
                default -> throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }
}
