package com.triphippie.tripService.model.trip;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TripPatchDto {
    private LocalDate startDate;

    private LocalDate endDate;

    private String preferences;

    private String description;
}
