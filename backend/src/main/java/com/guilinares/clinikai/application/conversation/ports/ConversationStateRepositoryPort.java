package com.guilinares.clinikai.application.conversation.ports;

import com.guilinares.clinikai.infrastructure.data.entities.ConversationEntity;

import java.util.UUID;

public interface ConversationStateRepositoryPort {
    void updateState(UUID conversationId, String state);
}
