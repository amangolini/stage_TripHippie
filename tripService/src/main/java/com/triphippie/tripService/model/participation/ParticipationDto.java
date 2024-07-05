package com.triphippie.tripService.model.participation;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParticipationDto {
    @NotNull private int participantId;

    @NotNull private Long tripId;
}
