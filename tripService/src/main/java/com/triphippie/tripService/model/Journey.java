package com.triphippie.tripService.model;

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

//    @Id
//    @Column(name = "trip_id", nullable = false)
//    private Long trip_id;
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE)
//    @Column(name = "id", nullable = false)
//    private Long id;
//
//    @MapsId("trip_id")
//    @ManyToOne
//    @JoinColumn(name="trip_id", referencedColumnName = "id", nullable = false)
//    private Trip trip;

    @ManyToOne
    @JoinColumn(name="trip_id", nullable = false)
    private Trip trip;

    @Column(nullable = false)
    private String destination;

    private String description;
}
