package com.guilinares.clinikai.application.conversation.usecases;

import com.guilinares.clinikai.application.clinic.ports.ClinicRepositoryPort;
import com.guilinares.clinikai.application.conversation.ports.ConversationRepositoryPort;
import com.guilinares.clinikai.application.conversation.ports.ConversationStateRepositoryPort;
import com.guilinares.clinikai.application.conversation.ports.MessageRepositoryPort;
import com.guilinares.clinikai.application.patient.ports.PatientRepositoryPort;
import com.guilinares.clinikai.domain.enums.Channel;
import com.guilinares.clinikai.domain.enums.ConversationStatus;
import com.guilinares.clinikai.domain.enums.MessageDirection;
import com.guilinares.clinikai.infrastructure.data.entities.ConversationEntity;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class RegisterConversationUseCase {

    private final ConversationRepositoryPort conversation;
    private final ConversationStateRepositoryPort conversationState;
    private final MessageRepositoryPort message;
    private final ClinicRepositoryPort clinic;
    private final PatientRepositoryPort patient;

    public ConversationResult execute(String clinicId, String patientId, String receivedMessage, MessageDirection direction) {
        UUID clinicIdUUID = UUID.fromString(clinicId);
        UUID patientIdUUID = UUID.fromString(patientId);
        var openConversation = conversation.findOpenConversation(clinicIdUUID, patientIdUUID, Channel.WHATSAPP);

        if (openConversation.isPresent()) {
            return handleExistingConversation(receivedMessage, direction, openConversation.get());
        } else {
            return handleNewConversation(receivedMessage, direction, clinicIdUUID, patientIdUUID);
        }
    }

    private ConversationResult handleExistingConversation(String receivedMessage, MessageDirection direction, ConversationEntity openConversation) {
        conversationState.updateState(openConversation.getId(), "IN_PROGRESS");
        message.pushMessage(openConversation, direction, receivedMessage);
        return new ConversationResult(openConversation.getId().toString());
    }

    private ConversationResult handleNewConversation(String receivedMessage, MessageDirection direction, UUID clinicIdUUID, UUID patientIdUUID) {
        var clinicRef = clinic.getReference(clinicIdUUID);
        var patientRef = patient.getReference(patientIdUUID);

        var conv = ConversationEntity.builder()
                .clinic(clinicRef)
                .patient(patientRef)
                .channel(Channel.WHATSAPP)
                .status(ConversationStatus.OPEN)
                .build();

        ConversationEntity saved =  conversation.save(conv);
        conversationState.updateState(saved.getId(), "INITIAL");
        message.pushMessage(saved, direction, receivedMessage);
        return new ConversationResult(saved.getId().toString());
    }

    public record ConversationResult(String conversationId) {}
}
