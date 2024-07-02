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

    private boolean validateDates(LocalDate start, LocalDate end) {
        return start.isBefore(end);
    }

    public void createTrip(Integer userId, TripInDto tripInDto) throws TripServiceException {
        if(!validateDates(tripInDto.getStartDate(), tripInDto.getEndDate())) throw new TripServiceException(TripServiceError.BAD_REQUEST);
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

    public void modifyTrip(Integer userId, Long id, TripInDto tripInDto) throws TripServiceException {
        if(!validateDates(tripInDto.getStartDate(), tripInDto.getEndDate())) throw new TripServiceException(TripServiceError.BAD_REQUEST);
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

    public void deleteTripById(Integer userId, Long id) throws TripServiceException {
        Optional<Trip> oldTrip = tripRepository.findById(id);
        if(oldTrip.isEmpty()) return;
        if(!oldTrip.get().getUserId().equals(userId)) throw new TripServiceException(TripServiceError.FORBIDDEN);
        tripRepository.deleteById(id);
    }

    public void createJourney(Integer userId, JourneyInDto journeyInDto) throws TripServiceException {
        Optional<Trip> trip = tripRepository.findById(journeyInDto.getTripId());
        if(trip.isEmpty()) throw new TripServiceException(TripServiceError.NOT_FOUND);
        if(!trip.get().getUserId().equals(userId)) throw new TripServiceException(TripServiceError.FORBIDDEN);

        Journey journey = new Journey();
        journey.setTrip(trip.get());
        journey.setDestination(journeyInDto.getDestination());
        journey.setDescription(journeyInDto.getDescription());
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

    public JourneyOutDto findJourneyById(Long id) throws TripServiceException {
        Optional<Journey> journey = journeyRepository.findById(id);
        if(journey.isEmpty()) throw new TripServiceException(TripServiceError.NOT_FOUND);
        return mapToJourneyOut(journey.get());
    }

    public void modifyJourney(Integer userId, Long id, JourneyUpdate journeyUpdate) throws TripServiceException {
        Optional<Journey> journey = journeyRepository.findById(id);
        if(journey.isEmpty()) throw new TripServiceException(TripServiceError.NOT_FOUND);
        if(!journey.get().getTrip().getUserId().equals(userId)) throw new TripServiceException(TripServiceError.FORBIDDEN);

        journey.get().setDestination(journeyUpdate.getDestination());
        journey.get().setDescription(journeyUpdate.getDescription());
        journeyRepository.save(journey.get());
    }

    public void deleteJourney(Integer userId, Long id) throws TripServiceException {
        Optional<Journey> journey = journeyRepository.findById(id);
        if(journey.isEmpty()) return;
        if(!journey.get().getTrip().getUserId().equals(userId)) throw new TripServiceException(TripServiceError.FORBIDDEN);
        journeyRepository.deleteById(id);
    }
}
