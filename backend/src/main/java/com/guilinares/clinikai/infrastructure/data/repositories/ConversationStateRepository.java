package com.guilinares.clinikai.infrastructure.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ConversationStateRepository extends JpaRepository<ConversationStateRepository, UUID> {

}
