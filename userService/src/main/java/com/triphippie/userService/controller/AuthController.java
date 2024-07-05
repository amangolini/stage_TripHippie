package com.triphippie.userService.controller;

import com.triphippie.userService.model.auth.AuthDto;
import com.triphippie.userService.model.auth.ValidateUserDto;
import com.triphippie.userService.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Controller
@RequestMapping("api/users")
public class AuthController {
    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /* AUTH MAPPERS */
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody @Valid AuthDto authDto) {
        return new ResponseEntity<>(authService.login(authDto), HttpStatus.OK);
    }

    @PostMapping("/validateToken")
    public ResponseEntity<ValidateUserDto> validateToken(@RequestParam(name = "token") String token) {
        Optional<ValidateUserDto> userId = authService.validateToken(token);
        if (userId.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");

        return new ResponseEntity<>(userId.get(), HttpStatus.OK);
    }
}
