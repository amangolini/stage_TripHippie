package com.triphippie.tripService.model.trip;

import com.triphippie.tripService.model.destination.DestinationInDto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TripInDto {
    @NotNull private LocalDate startDate;

    @NotNull private LocalDate endDate;

    @NotNull private TripVehicle vehicle;

    @NotNull private TripType type;

    @NotNull private DestinationInDto startDestination;

    @NotNull private DestinationInDto endDestination;

    private String description;
}
