package com.triphippie.tripService.model.participation;

import com.triphippie.tripService.model.trip.Trip;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@IdClass(ParticipationId.class)
public class Participation {
    @Id
    private int participantId;

    @Id
    @ManyToOne
    @JoinColumn(name="trip_id")
    private Trip trip;
}
