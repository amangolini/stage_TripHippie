package com.triphippie.tripService.model.journey;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class JourneyInDto {
    @NotNull private Long tripId;

    @NotNull private String destination;

    private String description;
}
