package com.triphippie.ollamaChatbotService.model;

import jakarta.validation.constraints.NotNull;

public record Query(@NotNull String query) {
}
