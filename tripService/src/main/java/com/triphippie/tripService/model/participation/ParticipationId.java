package com.triphippie.tripService.model.participation;

import com.triphippie.tripService.model.trip.Trip;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
public class ParticipationId implements Serializable {
    private int participantId;
    private Trip trip;
}
