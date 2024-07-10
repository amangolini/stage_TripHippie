package com.triphippie.tripService.repository;

import com.triphippie.tripService.model.journey.Journey;
import com.triphippie.tripService.model.trip.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JourneyRepository extends JpaRepository<Journey, Long> {
    public List<Journey> findByTrip(Trip trip);
}
