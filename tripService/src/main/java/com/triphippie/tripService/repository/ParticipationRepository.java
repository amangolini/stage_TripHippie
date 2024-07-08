package com.triphippie.tripService.repository;

import com.triphippie.tripService.model.participation.Participation;
import com.triphippie.tripService.model.participation.ParticipationId;
import com.triphippie.tripService.model.trip.Trip;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParticipationRepository extends JpaRepository<Participation, ParticipationId> {
    public List<Participation> findByTrip(Trip trip);

    @Transactional
    public void deleteByParticipantId(Integer participantId);
}
