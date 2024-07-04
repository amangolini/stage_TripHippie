package com.triphippie.tripService.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("USER-SERVICE")
public interface UserServiceInterface {
    @GetMapping("api/users/{id}")
    public ResponseEntity<Object> getUser(@PathVariable("id") Integer id);
}
