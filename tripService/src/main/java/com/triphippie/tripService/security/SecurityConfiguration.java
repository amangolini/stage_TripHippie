package com.triphippie.tripService.security;

import jakarta.servlet.DispatcherType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .csrf((csrf) -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .addFilterBefore(new AuthFilter(), BasicAuthenticationFilter.class)
                .authorizeHttpRequests((authorizeHttpRequests) ->
                        authorizeHttpRequests
                                .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                                .requestMatchers(HttpMethod.GET,"api/trips/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "api/journeys/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "api/participation/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "api/internal/trips/**").permitAll()
                                .anyRequest().access((authentication, object) -> {
                                    if(authentication.get() instanceof TripServiceAuthentication)
                                        return new AuthorizationDecision(true);
                                    else return new AuthorizationDecision(false);
                                })
                );

        return http.build();
    }
}
