package com.triphippie.tripService.service;

import com.triphippie.tripService.repository.ParticipationRepository;
import com.triphippie.tripService.repository.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InternalService {
    private final TripRepository tripRepository;
    private final ParticipationRepository participationRepository;

    @Autowired
    public InternalService(TripRepository tripRepository, ParticipationRepository participationRepository) {
        this.tripRepository = tripRepository;
        this.participationRepository = participationRepository;
    }

    public void deleteTripsByUserId(Integer userId) {
        tripRepository.deleteByUserId(userId);
        participationRepository.deleteByParticipantId(userId);
    }
}
