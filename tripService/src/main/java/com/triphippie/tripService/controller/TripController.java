package com.triphippie.tripService.controller;

import com.triphippie.tripService.model.trip.*;
import com.triphippie.tripService.service.TripService;
import com.triphippie.tripService.service.TripServiceException;
import jakarta.validation.Valid;
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
    private final TripService tripService;

    @Autowired
    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    @PostMapping
    public ResponseEntity<?> postTrip(
            @RequestBody @Valid TripInDto tripInDto
    ) {
        try{
            tripService.createTrip(tripInDto);
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
            @RequestParam("endDate") Optional<LocalDate> endDate,
            @RequestParam("userId") Optional<Integer> userId
    ) {
        return new ResponseEntity<>(tripService.findAllTrips(
                tripsSize,
                page,
                startDate.orElse(null),
                endDate.orElse(null),
                userId.orElse(null)
        ),HttpStatus.OK);
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
            @PathVariable("tripId") Long id,
            @RequestBody @Valid TripInDto tripInDto
    ) {
        try {
            tripService.replaceTrip(id, tripInDto);
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

    @PatchMapping("/{tripId}")
    public ResponseEntity<?> patchTrip(
            @PathVariable("tripId") Long id,
            @RequestBody @Valid TripPatchDto patchDto
    ) {
        try {
            tripService.updateTrip(id, patchDto);
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
            @PathVariable("tripId") Long id
    ) {
        try {
            tripService.deleteTripById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (TripServiceException e) {
            switch (e.getError()) {
                case FORBIDDEN -> throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Write access forbidden");
                default -> throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @GetMapping("/vehicles")
    public ResponseEntity<TripVehicle[]> getTripVehicles() {
        return new ResponseEntity<>(TripVehicle.values(), HttpStatus.OK);
    }

    @GetMapping("/types")
    public ResponseEntity<TripType[]> getTripTypes() {
        return new ResponseEntity<>(TripType.values(), HttpStatus.OK);
    }
}
