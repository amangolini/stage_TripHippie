package com.triphippie.tripService.repository;

import com.triphippie.tripService.model.trip.Trip;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {
    @Query("SELECT t FROM Trip t WHERE "
            + "t.startDate >= current_date AND "
            + "(CAST(:startDate AS DATE) IS NULL OR t.startDate > :startDate) AND "
            + "(CAST(:endDate AS DATE) IS NULL OR t.endDate < :endDate)")
    public Page<Trip> findByMonth(LocalDate startDate, LocalDate endDate, Pageable pageable);

    @Query("SELECT t FROM Trip t WHERE t.endDate < current_date")
    public List<Trip> findCompleted();
}
