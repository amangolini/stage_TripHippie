package com.triphippie.apiGateway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class AuthException extends ResponseStatusException {
    public AuthException(HttpStatus status) {
        super(status);
    }
}