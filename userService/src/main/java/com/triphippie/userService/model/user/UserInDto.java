package com.triphippie.userService.model.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserInDto {
    @NotEmpty @NotNull
    private String username;

    @NotEmpty @NotNull
    private String password;

    @NotEmpty @NotNull
    private String firstName;

    @NotEmpty @NotNull
    private String lastName;

    private LocalDate dateOfBirth;

    @Email
    private String email;

    private String about;

    private String city;
}
