package com.triphippie.tripService.model.journey;

import com.triphippie.tripService.model.destination.DestinationInDto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JourneyUpdate {
    @NotNull private DestinationInDto destination;

    private String description;
}
