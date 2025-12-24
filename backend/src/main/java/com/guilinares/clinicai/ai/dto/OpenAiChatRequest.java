package com.guilinares.clinicai.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

public class OpenAiChatRequest {

    private String model;
    private List<Message> messages;
    private ResponseFormat response_format;

    public OpenAiChatRequest(String model, List<Message> messages) {
        this.model = model;
        this.messages = messages;
        this.response_format = new ResponseFormat("json_object");
    }

    public String getModel() {
        return model;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public ResponseFormat getResponse_format() {
        return response_format;
    }

    @Getter
    @AllArgsConstructor
    public static class Message {
        private String role;
        private String content;
    }

    @Getter
    @AllArgsConstructor
    public static class ResponseFormat {
        private String type;
    }
}
