package com.triphippie.ollamaChatbotService.service;

import com.triphippie.ollamaChatbotService.model.Query;
import com.triphippie.ollamaChatbotService.model.Result;
import com.triphippie.ollamaChatbotService.security.PrincipalFacade;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
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
    public ChatbotService(
            PrincipalFacade principalFacade,
            Assistant assistant
    ) {
        this.principalFacade = principalFacade;
        this.assistant = assistant;
    }

    public Optional<Result> ask(Query query) {
        Integer principal = principalFacade.getPrincipal();

        UserMessage message = UserMessage.from(
                TextContent.from(query.query())
        );

        Result result = new Result(assistant.chat(principal, message));

        return Optional.of(result);
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