package com.triphippie.tripService.service;

import com.triphippie.tripService.model.*;
import com.triphippie.tripService.repository.JourneyRepository;
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
    private TripRepository tripRepository;
    private JourneyRepository journeyRepository;

    @Autowired
    public TripService(TripRepository tripRepository, JourneyRepository journeyRepository) {
        this.tripRepository = tripRepository;
        this.journeyRepository = journeyRepository;
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

    private static JourneyOutDto mapToJourneyOut(Journey journey) {
        return new JourneyOutDto(
                journey.getId(),
                journey.getDestination(),
                journey.getDescription()
        );
    }

    private boolean isTripPresent(Long tripId) {
        return tripRepository.findById(tripId).isPresent();
    }

    public void createTrip(TripInDto tripInDto) throws TripServiceException {
        if(tripInDto.getStartDate().isAfter(tripInDto.getEndDate())) throw new TripServiceException(TripServiceError.BAD_REQUEST);
        Trip trip = new Trip();
        trip.setUserId(tripInDto.getUserId());
        trip.setStartDate(tripInDto.getStartDate());
        trip.setEndDate(tripInDto.getEndDate());
        trip.setPreferencies(tripInDto.getPreferencies());
        trip.setDescription(tripInDto.getDescription());
        tripRepository.save(trip);
    }

    public List<TripOutDto> findAllTrips(
            Integer size,
            Integer page,
            @Nullable LocalDate startFilter,
            @Nullable LocalDate endFilter
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Trip> tripsPage = tripRepository.findByMonth(startFilter, endFilter, pageable);
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

    public void createJourney(Long tripId, String destination, @Nullable String description) throws TripServiceException {
        Optional<Trip> trip = tripRepository.findById(tripId);
        if(trip.isEmpty()) throw new TripServiceException(TripServiceError.NOT_FOUND);

        Journey journey = new Journey();
        journey.setTrip(trip.get());
        journey.setDestination(destination);
        journey.setDescription(description);
        journeyRepository.save(journey);
    }

    public List<JourneyOutDto> findJourneys(Long tripId) throws TripServiceException {
        Optional<Trip> trip = tripRepository.findById(tripId);
        if(trip.isEmpty()) throw new TripServiceException(TripServiceError.NOT_FOUND);

        List<Journey> journeys = journeyRepository.findByTrip(trip.get());
        List<JourneyOutDto> outJourneys = new ArrayList<>();
        for (Journey j : journeys) {
            outJourneys.add(mapToJourneyOut(j));
        }

        return outJourneys;
    }

    public JourneyOutDto findJourneyById(Long tripId, Long journeyId) throws TripServiceException {
        Optional<Journey> journey = journeyRepository.findById(new JourneyKey(tripId, journeyId));
        if(journey.isEmpty()) throw new TripServiceException(TripServiceError.NOT_FOUND);
        return mapToJourneyOut(journey.get());
    }
}
