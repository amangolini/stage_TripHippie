package com.triphippie.tripService.model.journey;

import com.triphippie.tripService.model.destination.Destination;
import com.triphippie.tripService.model.trip.Trip;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Journey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="trip_id", nullable = false)
    private Trip trip;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "destination_id", nullable = false)
    private Destination destination;

    private String description;
}
