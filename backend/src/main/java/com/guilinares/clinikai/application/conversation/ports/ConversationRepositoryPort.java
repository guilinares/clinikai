package com.guilinares.clinikai.application.conversation.ports;

import com.guilinares.clinikai.domain.enums.Channel;
import com.guilinares.clinikai.domain.enums.ConversationStatus;
import com.guilinares.clinikai.infrastructure.data.entities.ConversationEntity;

import java.util.Optional;
import java.util.UUID;

public interface ConversationRepositoryPort {
    Optional<ConversationEntity> findOpenConversation(UUID clinicId, UUID patientId, Channel channel);
    ConversationEntity save(ConversationEntity conversationEntity);
    ConversationEntity lockById(UUID conversationId);
}
