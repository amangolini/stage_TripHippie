package com.triphippie.tripService.model.journey;

import com.triphippie.tripService.model.destination.Destination;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JourneyOutDto {
    private Long id;

    private Integer stepNumber;

    private Destination destination;

    private String description;
}
