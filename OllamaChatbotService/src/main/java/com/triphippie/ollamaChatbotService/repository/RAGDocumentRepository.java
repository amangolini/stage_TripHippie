package com.triphippie.ollamaChatbotService.repository;

import com.triphippie.ollamaChatbotService.model.RAGDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RAGDocumentRepository extends JpaRepository<RAGDocument, Long> {
    public Optional<RAGDocument> findByNameAndUserId(String name, Integer userId);
}
