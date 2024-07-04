package com.triphippie.tripService.model.trip;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TripInDto {
    @NotNull private LocalDate startDate;

    @NotNull private LocalDate endDate;

    private String preferences;

    private String description;
}
