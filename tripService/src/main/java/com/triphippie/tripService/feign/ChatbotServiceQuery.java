package com.triphippie.tripService.feign;

import jakarta.validation.constraints.NotNull;

public record ChatbotServiceQuery(@NotNull String query) {
}
