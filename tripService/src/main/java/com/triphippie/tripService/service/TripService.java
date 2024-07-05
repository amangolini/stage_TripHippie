package com.triphippie.tripService.service;

import com.triphippie.tripService.model.trip.Trip;
import com.triphippie.tripService.model.trip.TripInDto;
import com.triphippie.tripService.model.trip.TripOutDto;
import com.triphippie.tripService.model.trip.TripPatchDto;
import com.triphippie.tripService.repository.TripRepository;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TripService {
    private final TripRepository tripRepository;

    @Autowired
    public TripService(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    private static TripOutDto mapToTripOut(Trip trip) {
        return new TripOutDto(
                trip.getId(),
                trip.getUserId(),
                trip.getStartDate(),
                trip.getEndDate(),
                trip.getPreferencies(),
                trip.getDescription()
        );
    }

    private boolean invalidDates(LocalDate start, LocalDate end) {
        return !start.isBefore(end);
    }

    public void createTrip(Integer userId, TripInDto tripInDto) throws TripServiceException {
        if(invalidDates(tripInDto.getStartDate(), tripInDto.getEndDate()))
            throw new TripServiceException(TripServiceError.BAD_REQUEST);

        Trip trip = new Trip();
        trip.setUserId(userId);
        trip.setStartDate(tripInDto.getStartDate());
        trip.setEndDate(tripInDto.getEndDate());
        trip.setPreferencies(tripInDto.getPreferences());
        trip.setDescription(tripInDto.getDescription());
        tripRepository.save(trip);
    }

    public List<TripOutDto> findAllTrips(
            Integer size,
            Integer page,
            @Nullable LocalDate startFilter,
            @Nullable LocalDate endFilter,
            @Nullable Integer userId
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Trip> tripsPage = tripRepository.findWithFilters(startFilter, endFilter, userId, pageable);
        List<TripOutDto> outTrips = new ArrayList<>();
        for (Trip u : tripsPage) {
            outTrips.add(mapToTripOut(u));
        }
        return outTrips;
    }

    public List<TripOutDto> findAllTripsCompleted() {
        List<Trip> trips = tripRepository.findCompleted();
        List<TripOutDto> outTrips = new ArrayList<>();
        for (Trip u : trips) {
            outTrips.add(mapToTripOut(u));
        }
        return outTrips;
    }

    public Optional<TripOutDto> findTripById(Long id) {
        Optional<Trip> trip = tripRepository.findById(id);
        return trip.map(TripService::mapToTripOut);
    }

    public void replaceTrip(Integer userId, Long id, TripInDto tripInDto) throws TripServiceException {
        if(invalidDates(tripInDto.getStartDate(), tripInDto.getEndDate())) throw new TripServiceException(TripServiceError.BAD_REQUEST);
        Optional<Trip> oldTrip = tripRepository.findById(id);
        if(oldTrip.isEmpty()) throw new TripServiceException(TripServiceError.NOT_FOUND);
        if(!oldTrip.get().getUserId().equals(userId)) throw new TripServiceException(TripServiceError.FORBIDDEN);

        Trip trip = oldTrip.get();
        trip.setStartDate(tripInDto.getStartDate());
        trip.setEndDate(tripInDto.getEndDate());
        trip.setPreferencies(tripInDto.getPreferences());
        trip.setDescription(tripInDto.getDescription());

        tripRepository.save(trip);
    }

    public void updateTrip(Integer userId, Long id, TripPatchDto patchDto) throws TripServiceException {
        Optional<Trip> oldTrip = tripRepository.findById(id);
        if(oldTrip.isEmpty()) throw new TripServiceException(TripServiceError.NOT_FOUND);
        if(!oldTrip.get().getUserId().equals(userId)) throw new TripServiceException(TripServiceError.FORBIDDEN);

        Trip trip = oldTrip.get();
        if(patchDto.getStartDate() != null) trip.setStartDate(patchDto.getStartDate());
        if(patchDto.getEndDate() != null) trip.setEndDate(patchDto.getEndDate());
        if(patchDto.getPreferences() != null) trip.setPreferencies(patchDto.getPreferences());
        if(patchDto.getDescription() != null) trip.setDescription(patchDto.getDescription());

        if(invalidDates(trip.getStartDate(), trip.getEndDate())) throw new TripServiceException(TripServiceError.BAD_REQUEST);

        tripRepository.save(trip);
    }

    public void deleteTripById(Integer userId, Long id) throws TripServiceException {
        Optional<Trip> oldTrip = tripRepository.findById(id);
        if(oldTrip.isEmpty()) return;
        if(!oldTrip.get().getUserId().equals(userId)) throw new TripServiceException(TripServiceError.FORBIDDEN);
        tripRepository.deleteById(id);
    }
}
