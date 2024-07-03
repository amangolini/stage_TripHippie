package com.triphippie.userService.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record AuthDto(
        @NotNull @NotEmpty String username,
        @NotNull @NotEmpty String password
) {
}
