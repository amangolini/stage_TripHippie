package com.triphippie.ollamaChatbotService.service;

public class ChatbotServiceException extends Exception {
    private ChatbotServiceError error;

    ChatbotServiceException(ChatbotServiceError error) {
        super();
        this.error = error;
    }

    public ChatbotServiceError getError() {
        return error;
    }

    public void setError(ChatbotServiceError error) {
        this.error = error;
    }
}
