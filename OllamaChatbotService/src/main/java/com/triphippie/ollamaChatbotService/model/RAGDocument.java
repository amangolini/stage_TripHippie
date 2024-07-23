package com.triphippie.ollamaChatbotService.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class RAGDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer userId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String path;

    @Column(nullable = false)
    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    private List<String> embeddings;
}
