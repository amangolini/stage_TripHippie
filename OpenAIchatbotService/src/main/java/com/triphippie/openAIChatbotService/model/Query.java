package com.triphippie.openAIChatbotService.model;

import jakarta.validation.constraints.NotNull;

public record Query(@NotNull String query) {
}
