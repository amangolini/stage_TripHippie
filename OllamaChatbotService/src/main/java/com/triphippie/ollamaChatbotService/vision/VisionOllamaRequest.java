package com.triphippie.ollamaChatbotService.vision;

import java.util.List;

public record VisionOllamaRequest(
        String model,
        String prompt,
        List<String> images,
        Boolean stream
) {
}
