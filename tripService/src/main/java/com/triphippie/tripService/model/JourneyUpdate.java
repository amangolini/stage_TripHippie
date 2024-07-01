package com.triphippie.tripService.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class JourneyUpdate {
    @NonNull private String destination;

    private String description;
}
