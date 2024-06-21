package com.triphippie.tripService.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class TripOutDto {
    private Long id;

    private Integer userId;

    private LocalDate startDate;

    private LocalDate endDate;

    private String preferencies;

    private String description;
}