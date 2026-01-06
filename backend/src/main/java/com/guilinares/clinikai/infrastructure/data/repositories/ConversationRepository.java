package com.guilinares.clinikai.infrastructure.data.repositories;

import com.guilinares.clinikai.domain.enums.Channel;
import com.guilinares.clinikai.domain.enums.ConversationStatus;
import com.guilinares.clinikai.infrastructure.data.entities.ConversationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ConversationRepository extends JpaRepository<ConversationEntity, UUID> {
    Optional<ConversationEntity> findByClinicIdAndPatientIdAndChannelAndStatus(
            UUID clinicID,
            UUID patientId,
            Channel channel,
            ConversationStatus conversationStatus
    );
}
