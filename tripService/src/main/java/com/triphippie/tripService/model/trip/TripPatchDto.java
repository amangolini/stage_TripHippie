package com.triphippie.tripService.model.trip;

import com.triphippie.tripService.model.destination.DestinationDto;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TripPatchDto {
    private LocalDate startDate;

    private LocalDate endDate;

    private TripVehicle vehicle;

    private TripType type;

    private DestinationDto startDestination;

    private DestinationDto endDestination;

    private String description;
}
