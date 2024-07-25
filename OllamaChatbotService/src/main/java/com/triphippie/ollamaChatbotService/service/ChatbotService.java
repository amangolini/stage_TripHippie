package com.triphippie.ollamaChatbotService.service;

import com.triphippie.ollamaChatbotService.model.Query;
import com.triphippie.ollamaChatbotService.model.RAGDocument;
import com.triphippie.ollamaChatbotService.model.Result;
import com.triphippie.ollamaChatbotService.repository.RAGDocumentRepository;
import com.triphippie.ollamaChatbotService.security.PrincipalFacade;
import com.triphippie.ollamaChatbotService.vision.VisionLanguageModel;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentBySentenceSplitter;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
public class ChatbotService {
    private static final List<String> SUPPORTED_MEDIA_TYPES = List.of("txt");
    private static final String DOCUMENTS_PATH = "src/main/resources/static/documents/";
    private final PrincipalFacade principalFacade;
    private final Assistant assistant;
    private final VisionLanguageModel visionLanguageModel;
    private final RAGDocumentRepository ragDocumentRepository;
    private final EmbeddingStore<TextSegment> embeddingStore;
    private final EmbeddingModel embeddingModel;

    @Autowired
    public ChatbotService(
            PrincipalFacade principalFacade,
            Assistant assistant,
            VisionLanguageModel visionLanguageModel,
            RAGDocumentRepository ragDocumentRepository,
            EmbeddingStore<TextSegment> embeddingStore,
            EmbeddingModel embeddingModel
    ) {
        this.principalFacade = principalFacade;
        this.assistant = assistant;
        this.visionLanguageModel = visionLanguageModel;
        this.ragDocumentRepository = ragDocumentRepository;
        this.embeddingStore = embeddingStore;
        this.embeddingModel = embeddingModel;
    }

    private List<String> ingestFile(String uploadPath) {
        Document document = FileSystemDocumentLoader.loadDocument(
                uploadPath,
                new TextDocumentParser()
        );

        DocumentSplitter splitter = new DocumentBySentenceSplitter(200, 20);//DocumentSplitters.recursive(100, 10);
        List<TextSegment> segments = splitter.split(document);
        List<Embedding> embeddings = embeddingModel.embedAll(segments).content();
        return embeddingStore.addAll(embeddings, segments);
    }

    private void removeEmbeddings(List<String> ids) {
        embeddingStore.removeAll(ids);
    }

    public Optional<Result> ask(Query query) {
        Integer principal = principalFacade.getPrincipal();

        UserMessage message = UserMessage.from(
                TextContent.from(query.query())
        );

        Result result = new Result(assistant.chat(principal, message));

        return Optional.of(result);
    }

    @Transactional
    public Long uploadDocument(MultipartFile file) throws ChatbotServiceException, IOException {
        if(file.isEmpty()) throw new ChatbotServiceException(ChatbotServiceError.BAD_REQUEST);

        String filename = file.getOriginalFilename();
        String extension = filename.substring(filename.lastIndexOf(".") + 1);
        if(!SUPPORTED_MEDIA_TYPES.contains(extension)) throw new ChatbotServiceException(ChatbotServiceError.UNSUPPORTED);

        Integer userId = principalFacade.getPrincipal();
        Files.createDirectories(Path.of(DOCUMENTS_PATH + "/" + userId));
        String uploadPath = "src/main/resources/static/documents/" + userId + "/" + filename;

        Files.copy(file.getInputStream(), Path.of(uploadPath), StandardCopyOption.REPLACE_EXISTING);

        List<String> ids = ingestFile(uploadPath);

        RAGDocument ragDocument = ragDocumentRepository.findByNameAndUserId(filename, userId).orElse(new RAGDocument());
        ragDocument.setName(filename);
        ragDocument.setUserId(userId);
        ragDocument.setPath(uploadPath);
        ragDocument.setEmbeddings(ids);

        return ragDocumentRepository.save(ragDocument).getId();
    }

    public FileSystemResource getDocument(Long id) throws ChatbotServiceException {
        Optional<RAGDocument> document = ragDocumentRepository.findById(id);
        if(document.isEmpty()) throw new ChatbotServiceException(ChatbotServiceError.NOT_FOUND);

        if(!document.get().getUserId().equals(principalFacade.getPrincipal()))
            throw new ChatbotServiceException(ChatbotServiceError.FORBIDDEN);

        if(Files.exists(Path.of(document.get().getPath())))
            return new FileSystemResource(document.get().getPath());
        else throw new ChatbotServiceException(ChatbotServiceError.NOT_FOUND);
    }

    @Transactional
    public void deleteDocument(Long id) throws ChatbotServiceException, IOException {
        Optional<RAGDocument> document = ragDocumentRepository.findById(id);
        if(document.isEmpty()) return;

        if(!document.get().getUserId().equals(principalFacade.getPrincipal()))
            throw new ChatbotServiceException(ChatbotServiceError.FORBIDDEN);

        String filePath = document.get().getPath();

        ragDocumentRepository.deleteById(id);

        removeEmbeddings(document.get().getEmbeddings());

        if (Files.exists(Path.of(filePath))) {
            Files.delete(Path.of(filePath));
        }
    }

    public Optional<Result> spotDestination (MultipartFile picture) throws IOException {
        String response = visionLanguageModel.generate(
                "Guess the tourist destination (city or at least country) where the given picture was taken. Answer with ONLY the name.",
                List.of(Base64.getEncoder().encodeToString(picture.getBytes()))
        );

        return Optional.of(new Result(response));
    }
}
