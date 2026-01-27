package com.guilinares.clinikai.infrastructure.data.repositories;

import com.guilinares.clinikai.infrastructure.data.entities.ConversationStateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ConversationStateRepository extends JpaRepository<ConversationStateEntity, UUID> {

}
