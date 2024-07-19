package com.triphippie.openAIChatbotService.service;

import com.triphippie.openAIChatbotService.model.Query;
import com.triphippie.openAIChatbotService.model.Result;
import com.triphippie.openAIChatbotService.security.PrincipalFacade;
import dev.ai4j.openai4j.OpenAiHttpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

@Service
public class ChatbotService {
    private static final List<String> SUPPORTED_MEDIA_TYPES = List.of("txt");
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

    public void uploadDocument(MultipartFile file) throws ChatbotServiceException, IOException {
        if(file.isEmpty()) throw new ChatbotServiceException(ChatbotServiceError.BAD_REQUEST);

        String filename = file.getOriginalFilename();
        String extension = filename.substring(filename.lastIndexOf(".") + 1);
        if(!SUPPORTED_MEDIA_TYPES.contains(extension)) throw new ChatbotServiceException(ChatbotServiceError.UNSUPPORTED);

        String uploadPath = "src/main/resources/static/documents/" + filename;
        Files.copy(file.getInputStream(), Path.of(uploadPath), StandardCopyOption.REPLACE_EXISTING);
    }
}
