package com.triphippie.tripService.model.journey;

import com.triphippie.tripService.model.destination.Destination;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JourneyOutDto {
    private Long id;

    private Destination destination;

    private String description;
}
