package com.triphippie.chatbotService.model;

import jakarta.validation.constraints.NotNull;

public record Query(@NotNull String query) {
}
