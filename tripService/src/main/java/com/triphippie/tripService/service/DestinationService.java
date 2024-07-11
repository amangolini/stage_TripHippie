package com.triphippie.tripService.service;

import com.triphippie.tripService.feign.ChatbotServiceInterface;
import com.triphippie.tripService.feign.ChatbotServiceQuery;
import com.triphippie.tripService.model.destination.Destination;
import com.triphippie.tripService.model.destination.DestinationInDto;
import com.triphippie.tripService.model.destination.DestinationOutDto;
import com.triphippie.tripService.repository.DestinationRepository;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class DestinationService {
    private final DestinationRepository destinationRepository;
    private final ChatbotServiceInterface chatbotServiceInterface;

    @Autowired
    public DestinationService(DestinationRepository destinationRepository, ChatbotServiceInterface chatbotServiceInterface) {
        this.destinationRepository = destinationRepository;
        this.chatbotServiceInterface = chatbotServiceInterface;
    }

    public static DestinationOutDto mapToDestinationOut(Destination destination) {
        return new DestinationOutDto(
                destination.getId(),
                destination.getName(),
                destination.getLatitude(),
                destination.getLongitude()
        );
    }

    public static Destination mapToDestination(DestinationInDto dto) {
        Destination destination = new Destination();
        destination.setName(dto.getName());
        destination.setLatitude(dto.getLatitude());
        destination.setLongitude(dto.getLongitude());
        return destination;
    }

    public Object getDestinationInfoFromId(Long id) throws TripServiceException {
        Optional<Destination> destination = destinationRepository.findById(id);
        if (destination.isPresent()) {
            try {
                return chatbotServiceInterface.postSummarize(new ChatbotServiceQuery(
                        destination.get().getName()
                )).getBody();
            } catch (FeignException.FeignServerException e) {
                throw new TripServiceException(TripServiceError.UNAVAILABLE);
            }
        }
        else throw new TripServiceException(TripServiceError.NOT_FOUND);
    }
}
