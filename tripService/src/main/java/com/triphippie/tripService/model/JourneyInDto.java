package com.triphippie.tripService.model;

import lombok.Data;
import lombok.NonNull;

@Data
public class JourneyInDto {
    @NonNull private Long tripId;

    @NonNull private String destination;

    private String description;
}
