package com.triphippie.tripService.model;

import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;

@Data
public class TripInDto {
    @NonNull private LocalDate startDate;

    @NonNull private LocalDate endDate;

    private String preferences;

    private String description;
}
