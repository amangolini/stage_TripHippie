package com.triphippie.chatbotService.service;

import com.triphippie.chatbotService.model.Query;
import com.triphippie.chatbotService.model.Result;
import com.triphippie.chatbotService.security.PrincipalFacade;
import dev.ai4j.openai4j.OpenAiHttpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ChatbotService {
    private final PrincipalFacade principalFacade;
    private final Assistant assistant;

    @Autowired
    public ChatbotService(PrincipalFacade principalFacade, Assistant assistant) {
        this.principalFacade = principalFacade;
        this.assistant = assistant;
    }

    public Optional<Result> ask(Query query) {
        try {
            Result result = new Result(assistant.chat(principalFacade.getPrincipal(), query.query()));
            return Optional.of(result);

        } catch (OpenAiHttpException e) {
            return Optional.empty();
        }
    }
}
