package com.triphippie.tripService.model.destination;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DestinationDto {
    @NotNull private String name;

    @NotNull private double latitude;

    @NotNull private double longitude;
}
