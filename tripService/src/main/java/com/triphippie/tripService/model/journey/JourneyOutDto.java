package com.triphippie.tripService.model.journey;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JourneyOutDto {
    private Long id;

    private String destination;

    private String description;
}
