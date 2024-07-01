package com.triphippie.chatbotService.service;

import com.triphippie.chatbotService.model.Query;
import com.triphippie.chatbotService.model.Result;
import dev.ai4j.openai4j.OpenAiHttpException;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ChatbotService {

    private final Assistant assistant;

    @Autowired
    public ChatbotService(Assistant assistant) {
        this.assistant = assistant;
    }

    public Optional<Result> ask(Query query, Integer userId) {
        try {
            Result result = new Result(assistant.chat(userId, query.query()));
            return Optional.of(result);

        } catch (OpenAiHttpException e) {
            return Optional.empty();
        }
    }
}
