package com.triphippie.tripService.service;

public class TripServiceException extends Exception {
    private TripServiceError error;

    TripServiceException(TripServiceError error) {
        super();
        this.error = error;
    }

    public TripServiceError getError() {
        return error;
    }

    public void setError(TripServiceError error) {
        this.error = error;
    }
}
