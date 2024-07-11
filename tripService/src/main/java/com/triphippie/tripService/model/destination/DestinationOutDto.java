package com.triphippie.tripService.model.destination;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DestinationOutDto {
    private Long id;

    private String name;

    private double latitude;

    private double longitude;
}
