package com.triphippie.tripService.service;

import com.triphippie.tripService.model.Trip;
import com.triphippie.tripService.model.TripInDto;
import com.triphippie.tripService.model.TripOutDto;
import com.triphippie.tripService.repository.TripRepository;
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
    private TripRepository tripRepository;

    @Autowired
    public TripService(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    private static TripOutDto mapToTripOut(Trip trip) {
        TripOutDto tripOutDto = new TripOutDto(
                trip.getId(),
                trip.getUserId(),
                trip.getStartDate(),
                trip.getEndDate(),
                trip.getPreferencies(),
                trip.getDescription()
        );
        return tripOutDto;
    }

    public TripServiceResult createTrip(TripInDto tripInDto) {
        if(tripInDto.getStartDate().isAfter(tripInDto.getEndDate())) return TripServiceResult.BAD_REQUEST;
        Trip trip = new Trip();
        trip.setUserId(tripInDto.getUserId());
        trip.setStartDate(tripInDto.getStartDate());
        trip.setEndDate(tripInDto.getEndDate());
        trip.setPreferencies(tripInDto.getPreferencies());
        trip.setDescription(tripInDto.getDescription());
        tripRepository.save(trip);

        return TripServiceResult.SUCCESS;
    }

    public List<TripOutDto> findAllTrips(
            Integer size,
            Integer page,
            Optional<LocalDate> startFilter,
            Optional<LocalDate> endFilter
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Trip> tripsPage = tripRepository.findByMonth(startFilter.orElse(null), endFilter.orElse(null), pageable);
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

//    public TripServiceResult modifyTrip(Long id, TripInDto tripInDto) {
//        Optional<Trip> oldTrip = tripRepository.findById(id);
//        if(oldTrip.isEmpty()) return TripServiceResult.NOT_FOUND;
//
//        Trip trip = oldTrip.get();
//
//    }

    public void deleteTripById(Long id) { tripRepository.deleteById(id); }
}
