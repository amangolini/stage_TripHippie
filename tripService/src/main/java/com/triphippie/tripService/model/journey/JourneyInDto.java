package com.triphippie.tripService.model.journey;

import com.triphippie.tripService.model.destination.DestinationDto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class JourneyInDto {
    @NotNull private Long tripId;

    @NotNull private Integer stepNumber;

    @NotNull private DestinationDto destination;

    private String description;
}
