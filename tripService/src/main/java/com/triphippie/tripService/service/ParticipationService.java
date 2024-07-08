package com.triphippie.tripService.service;

import com.triphippie.tripService.feign.UserServiceInterface;
import com.triphippie.tripService.model.participation.Participation;
import com.triphippie.tripService.model.participation.ParticipationDto;
import com.triphippie.tripService.model.participation.ParticipationId;
import com.triphippie.tripService.model.trip.Trip;
import com.triphippie.tripService.repository.ParticipationRepository;
import com.triphippie.tripService.repository.TripRepository;
import com.triphippie.tripService.security.PrincipalFacade;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ParticipationService {
    private final TripRepository tripRepository;
    private final ParticipationRepository participationRepository;
    private final UserServiceInterface userServiceInterface;
    private final PrincipalFacade principalFacade;

    @Autowired
    public ParticipationService(
            TripRepository tripRepository,
            ParticipationRepository participationRepository,
            UserServiceInterface userServiceInterface,
            PrincipalFacade principalFacade
    ) {
        this.tripRepository = tripRepository;
        this.participationRepository = participationRepository;
        this.userServiceInterface = userServiceInterface;
        this.principalFacade = principalFacade;
    }

    public void createParticipation(ParticipationDto inDto) throws TripServiceException {
        Integer userId = principalFacade.getPrincipal();

        Optional<Trip> trip = tripRepository.findById(inDto.getTripId());
        if(trip.isEmpty()) throw new TripServiceException(TripServiceError.NOT_FOUND);
        if(!trip.get().getUserId().equals(userId)) throw new TripServiceException(TripServiceError.FORBIDDEN);
        if(userId.equals(inDto.getParticipantId())) return;

        try {
            userServiceInterface.getUser(inDto.getParticipantId());
        } catch (FeignException.FeignClientException.NotFound e) {
            throw new TripServiceException(TripServiceError.NOT_FOUND);
        }

        Participation participation = new Participation();
        participation.setParticipantId(inDto.getParticipantId());
        participation.setTrip(trip.get());
        participationRepository.save(participation);
    }

    public List<ParticipationDto> findParticipation(Long tripId) throws TripServiceException {
        Optional<Trip> trip = tripRepository.findById(tripId);
        if(trip.isEmpty()) throw new TripServiceException(TripServiceError.NOT_FOUND);

        List<Participation> participation = participationRepository.findByTrip(trip.get());
        List<ParticipationDto> outParticipation = new ArrayList<>();
        for (Participation p : participation) {
            outParticipation.add(new ParticipationDto(p.getParticipantId(), p.getTrip().getId()));
        }

        return outParticipation;
    }

    public void deleteParticipation(ParticipationDto inDto) throws TripServiceException {
        Optional<Trip> trip = tripRepository.findById(inDto.getTripId());
        if(trip.isEmpty()) return;

        ParticipationId id = new ParticipationId(inDto.getParticipantId(), trip.get());
        Optional<Participation> participation = participationRepository.findById(id);
        if(participation.isEmpty()) return;

        if(!trip.get().getUserId().equals(principalFacade.getPrincipal()))
            throw new TripServiceException(TripServiceError.FORBIDDEN);

        participationRepository.deleteById(id);
    }
}
