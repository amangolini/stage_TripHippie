package com.triphippie.tripService.model.trip;

import com.triphippie.tripService.model.destination.DestinationDto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TripInDto {
    @NotNull private LocalDate startDate;

    @NotNull private LocalDate endDate;

    @NotNull private TripVehicle vehicle;

    @NotNull private TripType type;

    @NotNull private DestinationDto startDestination;

    @NotNull private DestinationDto endDestination;

    private String description;
}
