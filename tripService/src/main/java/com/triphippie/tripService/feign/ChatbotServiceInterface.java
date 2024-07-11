package com.triphippie.tripService.feign;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("CHATBOT-SERVICE")
public interface ChatbotServiceInterface {
    @PostMapping("api/internal/chatbot/summarize")
    public ResponseEntity<Object> postSummarize(@RequestBody @Valid ChatbotServiceQuery query);
}
