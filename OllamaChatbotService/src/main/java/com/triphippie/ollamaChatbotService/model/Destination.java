package com.triphippie.ollamaChatbotService.model;

import java.util.List;

public record Destination (
        String history,

        String culture,

        List<String> typicalDishes,

        String bestPeriodToVisit,

        List<String> museums,

        List<String> monuments
) {
}
