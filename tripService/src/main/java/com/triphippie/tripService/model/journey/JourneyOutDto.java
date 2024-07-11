package com.triphippie.tripService.model.journey;

import com.triphippie.tripService.model.destination.DestinationDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JourneyOutDto {
    private Long id;

    private DestinationDto destination;

    private String description;
}
