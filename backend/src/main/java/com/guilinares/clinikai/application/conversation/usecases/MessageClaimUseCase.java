package com.guilinares.clinikai.application.conversation.usecases;

import com.guilinares.clinikai.application.conversation.ports.ConversationRepositoryPort;
import com.guilinares.clinikai.application.conversation.ports.MessageRepositoryPort;
import com.guilinares.clinikai.infrastructure.data.entities.MessageEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class MessageClaimUseCase {

    private final ConversationRepositoryPort conversationRepositoryPort;
    private final MessageRepositoryPort messageRepositoryPort;

    @Transactional
    public ClaimResult execute(String conversationId, int maxMessages) {
        try {
            var conversation = conversationRepositoryPort.lockById(UUID.fromString(conversationId));
            var pending = messageRepositoryPort.findMessagesPendingForClaim(conversation.getId(), maxMessages);
            if (pending.isEmpty()) {
                return ClaimResult.nothingToDo();
            }
            var claimId = UUID.randomUUID();
            var now = OffsetDateTime.now();
            var ids = pending.stream().map(MessageEntity::getId).toList();
            var messageList = pending.stream().map(MessageEntity::getText).toList();

            int updated = messageRepositoryPort.claimByIds(ids, claimId, now);

            if (updated == 0) {
                return ClaimResult.nothingToDo();
            }

            return ClaimResult.claimed(claimId, messageList);
        } catch (Exception e) {
            return null;
        }
    }

    public record ClaimResult(boolean claimed, UUID claimId, List<String> messages) {
        static ClaimResult nothingToDo() { return new ClaimResult(false, null, List.of()); }
        static ClaimResult claimed(UUID claimId, List<String> messages) {
            return new ClaimResult(true, claimId, messages);
        }
    }
}
