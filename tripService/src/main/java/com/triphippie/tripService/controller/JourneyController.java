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
    public ResponseEntity<?> postJourney(@RequestBody JourneyInDto journeyInDto) {
        try {
            tripService.createJourney(journeyInDto);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (TripServiceException e) {
            return switch (e.getError()) {
                case NOT_FOUND -> new ResponseEntity<>(HttpStatus.NOT_FOUND);
                default -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            };
        }
    }

    @GetMapping()
    public ResponseEntity<?> getJourneys(@RequestParam("tripId") Long id) {
        try {
            return new ResponseEntity<>(tripService.findJourneys(id), HttpStatus.OK);
        } catch (TripServiceException e) {
            return switch (e.getError()) {
                case NOT_FOUND -> new ResponseEntity<>(HttpStatus.NOT_FOUND);
                default -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            };
        }
    }

    @GetMapping("/{journeyId}")
    public ResponseEntity<?> getJourney(@PathVariable("journeyId") Long id) {
        try {
            return new ResponseEntity<>(tripService.findJourneyById(id), HttpStatus.OK);
        } catch (TripServiceException e) {
            return switch (e.getError()) {
                case NOT_FOUND -> new ResponseEntity<>(HttpStatus.NOT_FOUND);
                default -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            };
        }
    }

    @PutMapping("/{journeyId}")
    public ResponseEntity<?> putJourney(
            @PathVariable("journeyId") Long id,
            @RequestBody JourneyUpdate update
    ) {
        try {
            tripService.modifyJourney(id, update);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (TripServiceException e) {
            return switch (e.getError()) {
                case NOT_FOUND -> new ResponseEntity<>(HttpStatus.NOT_FOUND);
                default -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            };
        }
    }

    @DeleteMapping("/{journeyId}")
    public ResponseEntity<?> deleteJourney(@PathVariable("journeyId") Long id) {
        tripService.deleteJourney(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
