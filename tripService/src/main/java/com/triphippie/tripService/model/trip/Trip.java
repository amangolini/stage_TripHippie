package com.triphippie.tripService.model.trip;

import com.triphippie.tripService.model.participation.Participation;
import com.triphippie.tripService.model.journey.Journey;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer userId;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    private String preferencies;

    private String description;

    @OneToMany(mappedBy = "trip", cascade = CascadeType.REMOVE)
    private List<Journey> journeys;

    @OneToMany(mappedBy = "trip", cascade = CascadeType.REMOVE)
    private List<Participation> participations;
}
