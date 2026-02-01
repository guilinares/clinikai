package com.guilinares.clinikai.application.conversation.ports;

import com.guilinares.clinikai.domain.enums.MessageDirection;
import com.guilinares.clinikai.infrastructure.data.entities.ConversationEntity;
import com.guilinares.clinikai.infrastructure.data.entities.MessageEntity;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface MessageRepositoryPort {
    void pushMessage(ConversationEntity conversationEntity, MessageDirection direction, String message);
    List<MessageEntity> findMessagesPendingForClaim(UUID conversationId, int maxMessages);
    int claimByIds(List<UUID> ids, UUID claimId, OffsetDateTime now);
    int markProcessedByClaimId(UUID claimId, OffsetDateTime now);
}
