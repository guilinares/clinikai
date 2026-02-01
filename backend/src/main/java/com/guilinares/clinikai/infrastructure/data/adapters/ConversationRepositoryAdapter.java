package com.guilinares.clinikai.infrastructure.data.adapters;

import com.guilinares.clinikai.application.conversation.exceptions.JaExisteConversaAbertaException;
import com.guilinares.clinikai.application.conversation.ports.ConversationRepositoryPort;
import com.guilinares.clinikai.domain.enums.Channel;
import com.guilinares.clinikai.domain.enums.ConversationStatus;
import com.guilinares.clinikai.infrastructure.data.entities.ConversationEntity;
import com.guilinares.clinikai.infrastructure.data.repositories.ConversationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;
@Component
@RequiredArgsConstructor
public class ConversationRepositoryAdapter implements ConversationRepositoryPort {

    private final ConversationRepository repository;

    @Override
    public Optional<ConversationEntity> findOpenConversation(UUID clinicId, UUID patientId, Channel channel) {
        return repository.findByClinicIdAndPatientIdAndChannelAndStatus(clinicId, patientId, channel, ConversationStatus.OPEN);
    }

    @Override
    public ConversationEntity save(ConversationEntity conversationEntity) {
        try {
            return repository.save(conversationEntity);
        } catch (DataIntegrityViolationException e) {
            throw new JaExisteConversaAbertaException();
        }
    }

    @Override
    public ConversationEntity lockById(UUID conversationId) {
        return repository.lockById(conversationId).orElseThrow(() -> new IllegalArgumentException("Conversation not found"));
    }
}
