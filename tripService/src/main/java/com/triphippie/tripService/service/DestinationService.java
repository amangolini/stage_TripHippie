package com.triphippie.tripService.service;

import com.triphippie.tripService.model.destination.Destination;
import com.triphippie.tripService.model.destination.DestinationDto;

public class DestinationService {
    public static DestinationDto mapToDestinationOut(Destination destination) {
        return new DestinationDto(
                destination.getName(),
                destination.getLatitude(),
                destination.getLongitude()
        );
    }

    public static Destination mapToDestination(DestinationDto dto) {
        Destination destination = new Destination();
        destination.setName(dto.getName());
        destination.setLatitude(destination.getLatitude());
        destination.setLongitude(destination.getLongitude());
        return destination;
    }
}
