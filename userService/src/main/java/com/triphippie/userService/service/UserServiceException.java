package com.triphippie.userService.service;

public class UserServiceException extends Exception {
    private UserServiceError error;

    UserServiceException(UserServiceError error) {
        super();
        this.error = error;
    }

    public UserServiceError getError() {
        return error;
    }

    public void setError(UserServiceError error) {
        this.error = error;
    }
}
