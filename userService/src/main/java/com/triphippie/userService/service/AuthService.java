package com.triphippie.userService.service;

import com.triphippie.userService.model.auth.AuthDto;
import com.triphippie.userService.model.auth.ValidateUserDto;
import com.triphippie.userService.model.user.User;
import com.triphippie.userService.repository.UserRepository;
import com.triphippie.userService.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Autowired
    public AuthService(UserRepository userRepository, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }


    /* SECURITY METHODS */
    public String login(AuthDto authDto) {
        Optional<User> user = userRepository.findByUsername(authDto.username());
        if(user.isEmpty()) throw new UsernameNotFoundException("User not found");

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authDto.username(), authDto.password())
        );
        //SecurityContextHolder.getContext().setAuthentication(auth);

        return "Bearer " + jwtService.generateToken(auth, Map.of("user-id", user.get().getId()));
    }

    public Optional<ValidateUserDto> validateToken(String token) {
        String username = jwtService.validateToken(token);
        Optional<User> user = userRepository.findByUsername(username);
        return user.isPresent()
                ? Optional.of(new ValidateUserDto(user.get().getId(), user.get().getRole().name()))
                : Optional.empty();
    }
}
