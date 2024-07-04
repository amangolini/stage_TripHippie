package com.triphippie.tripService.repository;

import com.triphippie.tripService.model.Participation;
import com.triphippie.tripService.model.ParticipationId;
import com.triphippie.tripService.model.trip.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParticipationRepository extends JpaRepository<Participation, ParticipationId> {
    public List<Participation> findByTrip(Trip trip);
}
