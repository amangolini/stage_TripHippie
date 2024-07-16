package com.triphippie.llmService.service;

import com.triphippie.llmService.model.Query;
import com.triphippie.llmService.model.Result;
import com.triphippie.llmService.security.PrincipalFacade;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Service
public class LLMService {
    private final ChatClient chatClient;
    private final PrincipalFacade principalFacade;

    @Autowired
    public LLMService(ChatClient.Builder builder, ChatMemory chatMemory, PrincipalFacade principalFacade) {
        this.principalFacade = principalFacade;

        this.chatClient = builder
                .defaultSystem("""
                    You're a helpful tour guide that provides information about the tourist attractions and culture
                    of the places you're asked about. Respond in a warm manner, suggesting possible activities to
                    do while on vacation at the specified place. If the question does not pertain the tourism sector,
                    say that you can't answer and explain why.
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
