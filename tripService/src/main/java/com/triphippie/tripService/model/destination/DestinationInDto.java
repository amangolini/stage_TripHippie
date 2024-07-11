package com.triphippie.tripService.model.destination;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DestinationInDto {
    @NotNull private String name;

    @NotNull private double latitude;

    @NotNull private double longitude;
}
