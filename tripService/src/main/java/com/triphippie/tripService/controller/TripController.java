package com.triphippie.tripService.controller;

import com.triphippie.tripService.model.TripInDto;
import com.triphippie.tripService.model.TripOutDto;
import com.triphippie.tripService.service.TripService;
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
        return switch (tripService.createTrip(tripInDto)) {
            case SUCCESS -> new ResponseEntity<>(HttpStatus.CREATED);
            case BAD_REQUEST -> new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            default -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        };
    }

    @GetMapping
    public ResponseEntity<?> getTrips(
            @RequestParam("tripsSize") int tripsSize,
            @RequestParam("page") int page,
            @RequestParam("startDate") Optional<LocalDate> startDate,
            @RequestParam("endDate") Optional<LocalDate> endDate
    ) {
        return new ResponseEntity<>(tripService.findAllTrips(tripsSize, page, startDate, endDate), HttpStatus.OK);
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

//    @PutMapping("/{tripId}")
//    public ResponseEntity<?> putTrip(@PathVariable("tripId") Long id, @RequestBody TripInDto tripInDto) {
//        TripServiceResult serviceResult = tripService.
//
//    }

    @DeleteMapping("/{tripId}")
    public ResponseEntity<?> deleteTrip(@PathVariable("tripId") Long id) {
        tripService.deleteTripById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
