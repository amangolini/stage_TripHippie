package com.triphippie.tripService.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class PrincipalFacade {
    public Integer getPrincipal() {
        return (Integer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
