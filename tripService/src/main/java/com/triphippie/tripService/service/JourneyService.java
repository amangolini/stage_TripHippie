package com.triphippie.tripService.service;

import com.triphippie.tripService.model.journey.Journey;
import com.triphippie.tripService.model.journey.JourneyInDto;
import com.triphippie.tripService.model.journey.JourneyOutDto;
import com.triphippie.tripService.model.journey.JourneyUpdate;
import com.triphippie.tripService.model.trip.Trip;
import com.triphippie.tripService.repository.JourneyRepository;
import com.triphippie.tripService.repository.TripRepository;
import com.triphippie.tripService.security.PrincipalFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class JourneyService {
    private final TripRepository tripRepository;
    private final JourneyRepository journeyRepository;
    private final PrincipalFacade principalFacade;

    @Autowired
    public JourneyService(TripRepository tripRepository, JourneyRepository journeyRepository, PrincipalFacade principalFacade) {
        this.tripRepository = tripRepository;
        this.journeyRepository = journeyRepository;
        this.principalFacade = principalFacade;
    }

    private static JourneyOutDto mapToJourneyOut(Journey journey) {
        return new JourneyOutDto(
                journey.getId(),
                journey.getDestination(),
                journey.getDescription()
        );
    }

    public void createJourney(JourneyInDto journeyInDto) throws TripServiceException {
        Optional<Trip> savedTrip = tripRepository.findById(journeyInDto.getTripId());
        if(savedTrip.isEmpty()) throw new TripServiceException(TripServiceError.NOT_FOUND);
        if(!savedTrip.get().getUserId().equals(principalFacade.getPrincipal()))
            throw new TripServiceException(TripServiceError.FORBIDDEN);

        Journey journey = new Journey();
        Trip trip = savedTrip.get();

        journey.setTrip(trip);
        journey.setDestination(journeyInDto.getDestination());
        journey.setDescription(journeyInDto.getDescription());

        if(trip.getJourneys().size() < journeyInDto.getStepNumber()) trip.getJourneys().add(journey);
        else trip.getJourneys().add(journeyInDto.getStepNumber(), journey);

        tripRepository.save(trip);
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

    public void modifyJourney(Long id, JourneyUpdate journeyUpdate) throws TripServiceException {
        Optional<Journey> savedJourney = journeyRepository.findById(id);
        if(savedJourney.isEmpty()) throw new TripServiceException(TripServiceError.NOT_FOUND);
        if(!savedJourney.get().getTrip().getUserId().equals(principalFacade.getPrincipal()))
            throw new TripServiceException(TripServiceError.FORBIDDEN);

        Journey journey = savedJourney.get();
        Trip trip = journey.getTrip();
        int journeyPosition = trip.getJourneys().indexOf(journey);

        journey.setDestination(journeyUpdate.getDestination());
        journey.setDescription(journeyUpdate.getDescription());
        trip.getJourneys().add(journeyUpdate.getStepNumber(), trip.getJourneys().remove(journeyPosition));

        tripRepository.save(trip);
    }

    public void deleteJourney(Long id) throws TripServiceException {
        Optional<Journey> journey = journeyRepository.findById(id);
        if(journey.isEmpty()) return;
        if(!journey.get().getTrip().getUserId().equals(principalFacade.getPrincipal()))
            throw new TripServiceException(TripServiceError.FORBIDDEN);
        journeyRepository.deleteById(id);

        Trip trip = journey.get().getTrip();
        trip.getJourneys().removeIf(Objects::isNull);
        tripRepository.save(trip);
    }
}
