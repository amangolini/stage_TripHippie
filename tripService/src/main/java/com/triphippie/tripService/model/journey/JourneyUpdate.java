package com.triphippie.tripService.model.journey;

import com.triphippie.tripService.model.destination.Destination;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JourneyUpdate {
    @NotNull private Integer stepNumber;

    @NotNull private Destination destination;

    private String description;
}
