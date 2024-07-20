package com.triphippie.ollamaChatbotService.service;

import com.triphippie.ollamaChatbotService.configuration.UserContext;
import com.triphippie.ollamaChatbotService.model.Conversation;
import com.triphippie.ollamaChatbotService.model.Query;
import com.triphippie.ollamaChatbotService.model.Result;
import com.triphippie.ollamaChatbotService.repository.ConversationRepository;
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
    private final ConversationRepository conversationRepository;

    @Autowired
    private UserContext userContext;

    @Autowired
    public ChatbotService(
            PrincipalFacade principalFacade,
            Assistant assistant,
            ConversationRepository conversationRepository
    ) {
        this.principalFacade = principalFacade;
        this.assistant = assistant;
        this.conversationRepository = conversationRepository;
    }

    public Optional<Result> ask(Query query) {
        Integer principal = principalFacade.getPrincipal();

        Optional<Conversation> conversation = conversationRepository.findById(principal);
        userContext.setContext(conversation.map(Conversation::getContext).orElse(null));

        UserMessage message = UserMessage.from(
                TextContent.from(query.query() +
                        ". IF you were talking about a place," +
                        " at the end write the place of which you're talking about inside square brackets," +
                        " OTHERWISE at the end write '[none]'"
                )
        );

        String chat = assistant.chat(principal, message);
        String context = chat.substring(chat.indexOf("[") + 1, chat.indexOf("]"));
        conversationRepository.save(new Conversation(principal, context));

        userContext.setContext(null);

        Result result = new Result(chat.substring(0, chat.indexOf("[")).stripTrailing());
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
