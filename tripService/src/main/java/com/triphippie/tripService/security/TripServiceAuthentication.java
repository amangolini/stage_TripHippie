package com.triphippie.tripService.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

public class TripServiceAuthentication implements Authentication {
    private final Integer userId;
    private final String userRole;
    private boolean valid;

    public TripServiceAuthentication(Integer userId, String userRole) {
        this.userId = userId;
        this.userRole = userRole;
        valid = true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(userRole));
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Integer getPrincipal() {
        return userId;
    }

    @Override
    public boolean isAuthenticated() {
        return valid;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        valid = isAuthenticated;
    }

    @Override
    public String getName() {
        return null;
    }
}
