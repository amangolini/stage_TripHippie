package com.triphippie.tripService.model.journey;

import com.triphippie.tripService.model.destination.DestinationDto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JourneyUpdate {
    @NotNull private Integer stepNumber;

    @NotNull private DestinationDto destination;

    private String description;
}
