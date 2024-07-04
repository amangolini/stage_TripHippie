package com.triphippie.tripService.model.journey;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JourneyUpdate {
    @NotNull private String destination;

    private String description;
}
