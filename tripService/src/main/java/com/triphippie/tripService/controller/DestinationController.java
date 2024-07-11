package com.triphippie.tripService.controller;

import com.triphippie.tripService.service.DestinationService;
import com.triphippie.tripService.service.TripServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Controller
@RequestMapping("api/destinations")
public class DestinationController {
    private final DestinationService destinationService;

    @Autowired
    public DestinationController(DestinationService destinationService) {
        this.destinationService = destinationService;
    }

    @GetMapping("/info/{destId}")
    public ResponseEntity<?> getInfo(@PathVariable(name = "destId") Long id) {
        try{
            Object info = destinationService.getDestinationInfoFromId(id);
            return new ResponseEntity<>(info, HttpStatus.OK);
        } catch (TripServiceException e) {
            switch (e.getError()) {
                case NOT_FOUND -> throw new ResponseStatusException(HttpStatus.NOT_FOUND);
                case UNAVAILABLE -> throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
                default -> throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }
}
