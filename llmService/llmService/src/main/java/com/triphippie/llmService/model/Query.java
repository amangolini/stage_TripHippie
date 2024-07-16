package com.triphippie.llmService.model;

import jakarta.validation.constraints.NotNull;

public record Query(@NotNull String query) {
}
