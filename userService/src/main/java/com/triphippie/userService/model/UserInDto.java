package com.triphippie.userService.model;

import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;

@Data
public class UserInDto {
    @NonNull private String username;

    @NonNull private String password;

    @NonNull private String firstName;

    @NonNull private String lastName;

    private LocalDate dateOfBirth;

    private String email;

    private String about;

    private String city;
}
