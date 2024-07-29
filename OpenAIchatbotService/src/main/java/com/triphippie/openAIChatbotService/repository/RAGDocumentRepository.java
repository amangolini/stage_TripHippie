package com.triphippie.openAIChatbotService.repository;

import com.triphippie.openAIChatbotService.model.RAGDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RAGDocumentRepository extends JpaRepository<RAGDocument, Long> {
    public Optional<RAGDocument> findByNameAndUserId(String name, Integer userId);
    public List<RAGDocument> findByUserId(Integer userId);
}
