package com.triphippie.ollamaChatbotService.vision;

import java.util.List;

public interface VisionLanguageModel {
    String generate(String prompt, List<String> images);
}
