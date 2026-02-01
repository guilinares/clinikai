package com.guilinares.clinikai.infrastructure.data.repositories;

import com.guilinares.clinikai.domain.enums.Channel;
import com.guilinares.clinikai.domain.enums.ConversationStatus;
import com.guilinares.clinikai.infrastructure.data.entities.ConversationEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ConversationRepository extends JpaRepository<ConversationEntity, UUID> {
    Optional<ConversationEntity> findByClinicIdAndPatientIdAndChannelAndStatus(
            UUID clinicID,
            UUID patientId,
            Channel channel,
            ConversationStatus conversationStatus
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from ConversationEntity c where c.id = :id")
    Optional<ConversationEntity> lockById(@Param("id") UUID id);
}
