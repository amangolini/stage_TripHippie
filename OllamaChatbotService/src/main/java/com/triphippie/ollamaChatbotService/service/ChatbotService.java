package com.triphippie.ollamaChatbotService.service;

import com.triphippie.ollamaChatbotService.model.Query;
import com.triphippie.ollamaChatbotService.model.Result;
import com.triphippie.ollamaChatbotService.security.PrincipalFacade;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Service
public class ChatbotService {
    private static final List<String> SUPPORTED_MEDIA_TYPES = List.of("txt");
    private final ChatClient chatClient;
    private final PrincipalFacade principalFacade;

    @Autowired
    public ChatbotService(ChatClient.Builder builder, ChatMemory chatMemory, PrincipalFacade principalFacade) {
        this.principalFacade = principalFacade;

        this.chatClient = builder
                .defaultSystem("""
                    You're a helpful tour guide that provides information about the tourist attractions and culture
                    of the places you're asked about. Respond in a warm manner, suggesting possible activities to
                    do while on vacation at the specified place. If the question DOES NOT pertain the tourism sector,
                    say that it is out of your area of expertise.
                    The response must have a maximum of 50 words.
                    """
                )
                .defaultAdvisors(
                        new PromptChatMemoryAdvisor(chatMemory)
                )
                .build();
    }

    public Optional<Result> ask(Query query) {
        try {
            String content = this.chatClient.prompt()
                    .user(query.query())
                    .advisors(a -> a
                            .param(CHAT_MEMORY_CONVERSATION_ID_KEY, principalFacade.getPrincipal())
                            .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100))
                    .call().content();
            return Optional.of(new Result(content));
        } catch (RuntimeException ex) {
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

    /*
    public Flux<ChatResponse> stream(Query query) {
        Message userMessage = new UserMessage(query.query());

        String systemText = """
            You are a helpful AI assistant that helps people find information.
            Your name is {name}
            You should reply in the style of a {voice}.
            Use a maximum of 80 words.
            """;

        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemText);
        Message systemMessage = systemPromptTemplate.createMessage(Map.of("name", "Waldo", "voice", "ghost"));

        Prompt prompt = new Prompt(List.of(userMessage, systemMessage));

        return ollamaChatModel.stream(prompt);
    }*/
}
