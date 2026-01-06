package com.guilinares.clinikai.infrastructure.data.repositories;

import com.guilinares.clinikai.infrastructure.data.entities.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.List;

public interface MessageRepository extends JpaRepository<MessageEntity, UUID> {
    List<MessageEntity> findTop20ByConversationIdOrderByCreatedAtDesc(UUID conversationId);
}
