package com.triphippie.tripService.model.trip;

import com.triphippie.tripService.model.destination.Destination;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TripInDto {
    @NotNull private LocalDate startDate;

    @NotNull private LocalDate endDate;

    @NotNull private TripVehicle vehicle;

    @NotNull private TripType type;

    @NotNull private Destination startDestination;

    @NotNull private Destination endDestination;

    private String description;
}
