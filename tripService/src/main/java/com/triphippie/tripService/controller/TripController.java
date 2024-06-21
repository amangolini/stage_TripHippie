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
    public ResponseEntity<?> postTrip(@RequestBody TripInDto tripInDto) {
        try{
            tripService.createTrip(tripInDto);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (TripServiceException e) {
            return switch (e.getError()) {
                case BAD_REQUEST -> new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                default -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            };
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
        return (trip.isPresent())
                ? new ResponseEntity<>(trip.get(), HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{tripId}")
    public ResponseEntity<?> putTrip(@PathVariable("tripId") Long id, @RequestBody TripInDto tripInDto) {
        try {
            tripService.modifyTrip(id, tripInDto);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (TripServiceException e) {
            return switch (e.getError()) {
                case BAD_REQUEST -> new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                case NOT_FOUND -> new ResponseEntity<>(HttpStatus.NOT_FOUND);
                default -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            };
        }
    }

    @DeleteMapping("/{tripId}")
    public ResponseEntity<?> deleteTrip(@PathVariable("tripId") Long id) {
        tripService.deleteTripById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
