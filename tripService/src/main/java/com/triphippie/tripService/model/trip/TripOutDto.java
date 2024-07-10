package com.triphippie.tripService.model.trip;

import com.triphippie.tripService.model.destination.Destination;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class TripOutDto {
    private Long id;

    private Integer userId;

    private LocalDate startDate;

    private LocalDate endDate;

    private TripVehicle vehicle;

    private TripType type;

    private Destination startDestination;

    private Destination endDestination;

    private String description;
}
