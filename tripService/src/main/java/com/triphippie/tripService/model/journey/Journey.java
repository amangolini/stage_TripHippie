package com.triphippie.tripService.model.journey;

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

    @Column(nullable = false)
    private String destination;

    private String description;
}
