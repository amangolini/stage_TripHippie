package com.triphippie.userService.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserInDto {
    private String username;

    private String password;

    private String firstName;

    private String lastName;

    private LocalDate dateOfBirth;

    private String email;

    private String about;

    private String city;
}
