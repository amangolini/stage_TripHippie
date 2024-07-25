package com.triphippie.ollamaChatbotService.vision;

import com.google.gson.Gson;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

public class OllamaVisionLanguageModel implements VisionLanguageModel{
    private final String baseUrl;
    private final String modelName;

    public OllamaVisionLanguageModel(
            String baseUrl,
            String modelName
    ) {
        this.baseUrl = baseUrl;
        this.modelName = modelName;
    }

    public String generate(String prompt, List<String> images) {
        String url = baseUrl + "/api/generate";
        RestTemplate restTemplate = new RestTemplate();
        Gson gson = new Gson();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        VisionOllamaRequest req = new VisionOllamaRequest(modelName, prompt, images, false);

        HttpEntity<String> entity = new HttpEntity<String>(gson.toJson(req), headers);
        String response = restTemplate.postForObject(url, entity, String.class);

        Map map = gson.fromJson(response, Map.class);

        return map.get("response").toString().trim();
    }
}
