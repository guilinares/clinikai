package com.guilinares.clinikai.application.conversation.usecases;

import com.guilinares.clinikai.application.conversation.ports.MessageRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@RequiredArgsConstructor
public class MarkMessageProcessedUseCase {

    private final MessageRepositoryPort messageRepositoryPort;

    @Transactional
    public int execute(String claimId) {
        return messageRepositoryPort.markProcessedByClaimId(UUID.fromString(claimId), OffsetDateTime.now());
    }
}
