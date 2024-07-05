package com.triphippie.userService.model.user;

import jakarta.validation.constraints.Email;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserPatchDto {
    private String username;

    private String password;

    private String firstName;

    private String lastName;

    private LocalDate dateOfBirth;

    @Email
    private String email;

    private String about;

    private String city;
}
