package com.triphippie.ollamaChatbotService.repository;

import com.triphippie.ollamaChatbotService.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationRepository extends JpaRepository<Conversation, Integer> {
}
