package com.triphippie.userService.model.auth;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record AuthDto(
        @NotNull @NotEmpty String username,
        @NotNull @NotEmpty String password
) {
}
