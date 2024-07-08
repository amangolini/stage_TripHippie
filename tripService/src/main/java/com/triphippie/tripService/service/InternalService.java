package com.triphippie.tripService.service;

import com.triphippie.tripService.repository.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InternalService {
    private final TripRepository tripRepository;

    @Autowired
    public InternalService(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    public void deleteTripsByUserId(Integer userId) {
        tripRepository.deleteByUserId(userId);
    }
}
