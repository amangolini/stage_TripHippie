package com.triphippie.tripService.repository;

import com.triphippie.tripService.model.Journey;
import com.triphippie.tripService.model.JourneyKey;
import com.triphippie.tripService.model.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JourneyRepository extends JpaRepository<Journey, JourneyKey> {
    public List<Journey> findByTrip(Trip trip);
}
