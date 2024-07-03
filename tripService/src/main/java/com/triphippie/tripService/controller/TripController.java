package com.triphippie.tripService.controller;

import com.triphippie.tripService.model.TripInDto;
import com.triphippie.tripService.model.TripOutDto;
import com.triphippie.tripService.service.TripService;
import com.triphippie.tripService.service.TripServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Optional;

@Controller
@RequestMapping("api/trips")
public class TripController {
    private TripService tripService;

    @Autowired
    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    @PostMapping
    public ResponseEntity<?> postTrip(
            @RequestHeader("auth-user-id") Integer userId,
            @RequestBody TripInDto tripInDto
    ) {
        try{
            tripService.createTrip(userId, tripInDto);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (TripServiceException e) {
            switch (e.getError()) {
                case BAD_REQUEST -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
                default -> throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @GetMapping
    public ResponseEntity<?> getTrips(
            @RequestParam("tripsSize") int tripsSize,
            @RequestParam("page") int page,
            @RequestParam("startDate") Optional<LocalDate> startDate,
            @RequestParam("endDate") Optional<LocalDate> endDate
    ) {
        return new ResponseEntity<>(tripService.findAllTrips(tripsSize, page, startDate.orElse(null), endDate.orElse(null)), HttpStatus.OK);
    }

    @GetMapping("/completed")
    public ResponseEntity<?> getCompletedTrips() {
        return new ResponseEntity<>(tripService.findAllTripsCompleted(), HttpStatus.OK);
    }

    @GetMapping("/{tripId}")
    public ResponseEntity<?> getTrip(@PathVariable("tripId") Long id) {
        Optional<TripOutDto> trip = tripService.findTripById(id);
        if(trip.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(trip.get(), HttpStatus.OK);
    }

    @PutMapping("/{tripId}")
    public ResponseEntity<?> putTrip(
            @RequestHeader("auth-user-id") Integer userId,
            @PathVariable("tripId") Long id,
            @RequestBody TripInDto tripInDto
    ) {
        try {
            tripService.modifyTrip(userId, id, tripInDto);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (TripServiceException e) {
            switch (e.getError()) {
                case BAD_REQUEST -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
                case NOT_FOUND -> throw new ResponseStatusException(HttpStatus.NOT_FOUND);
                case FORBIDDEN -> throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Write access forbidden");
                default -> throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @DeleteMapping("/{tripId}")
    public ResponseEntity<?> deleteTrip(
            @RequestHeader("auth-user-id") Integer userId,
            @PathVariable("tripId") Long id
    ) {
        try {
            tripService.deleteTripById(userId, id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (TripServiceException e) {
            switch (e.getError()) {
                case FORBIDDEN -> throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Write access forbidden");
                default -> throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }
}
