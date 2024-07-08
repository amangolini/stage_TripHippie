package com.triphippie.userService.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient("TRIP-SERVICE")
public interface TripServiceInterface {
    @PostMapping("api/internal/trips/deletedUser/{userId}")
    public ResponseEntity<Object> deleteTripsByUser(@PathVariable("userId") Integer id);
}
