package com.guilinares.clinikai.infrastructure.data.adapters;

import com.guilinares.clinikai.application.conversation.ports.MessageRepositoryPort;
import com.guilinares.clinikai.domain.enums.MessageDirection;
import com.guilinares.clinikai.infrastructure.data.entities.ConversationEntity;
import com.guilinares.clinikai.infrastructure.data.entities.MessageEntity;
import com.guilinares.clinikai.infrastructure.data.repositories.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.bridge.Message;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MessageRepositoryAdapter implements MessageRepositoryPort {

    private final MessageRepository messageRepository;

    @Override
    public void pushMessage(ConversationEntity conversationEntity, MessageDirection direction, String message) {
        var messageEntity = MessageEntity.builder()
                .conversation(conversationEntity)
                .direction(direction)
                .text(message)
                .build();
        messageRepository.save(messageEntity);

    }

    @Override
    public List<MessageEntity> findMessagesPendingForClaim(UUID conversationId, int maxMessages) {
        return messageRepository.findPendingForClaim(
                conversationId,
                MessageDirection.IN,
                PageRequest.of(0, maxMessages)
        );
    }

    @Override
    public int claimByIds(List<UUID> ids, UUID claimId, OffsetDateTime now) {
        return messageRepository.claimByIds(ids, claimId, now);
    }

    @Override
    public int markProcessedByClaimId(UUID claimId, OffsetDateTime now) {
        return messageRepository.markProcessedByClaimId(claimId, now);
    }
}
