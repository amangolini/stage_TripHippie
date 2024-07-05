package com.triphippie.userService.model.user;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class UserOutDto {
    private int id;

    private String username;

    private String firstName;

    private String lastName;

    private LocalDate dateOfBirth;

    private String email;

    private String about;

    private String city;
}
