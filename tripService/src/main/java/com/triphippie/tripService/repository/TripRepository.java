package com.triphippie.tripService.repository;

import com.triphippie.tripService.model.trip.Trip;
import jakarta.transaction.Transactional;
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
            + "(CAST(:endDate AS DATE) IS NULL OR t.endDate < :endDate) AND "
            + "(CAST(:userId AS INTEGER) IS NULL OR t.userId = :userId)"
    )
    public Page<Trip> findWithFilters(LocalDate startDate, LocalDate endDate, Integer userId, Pageable pageable);

    @Query("SELECT t FROM Trip t WHERE t.endDate < current_date")
    public List<Trip> findCompleted();

    @Transactional
    public void deleteByUserId(Integer userId);
}
