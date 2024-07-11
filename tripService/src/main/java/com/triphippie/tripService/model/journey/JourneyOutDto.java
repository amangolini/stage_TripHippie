package com.triphippie.tripService.model.journey;

import com.triphippie.tripService.model.destination.DestinationOutDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JourneyOutDto {
    private Long id;

    private DestinationOutDto destination;

    private String description;
}
