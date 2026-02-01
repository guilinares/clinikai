package com.guilinares.clinikai.infrastructure.data.adapters;

import com.guilinares.clinikai.application.conversation.ports.ConversationStateRepositoryPort;
import com.guilinares.clinikai.infrastructure.data.entities.ConversationEntity;
import com.guilinares.clinikai.infrastructure.data.entities.ConversationStateEntity;
import com.guilinares.clinikai.infrastructure.data.repositories.ConversationStateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ConversationStateRepositoryAdapter implements ConversationStateRepositoryPort {

    private final ConversationStateRepository conversationStateRepository;

    @Override
    public void updateState(UUID conversationId, String state) {
        var entity = conversationStateRepository.findById(conversationId).orElse(
               ConversationStateEntity.builder()
                        .conversation(ConversationEntity.builder()
                                .id(conversationId).build())
                        .build()
        );
        entity.setState(state);
        conversationStateRepository.save(entity);
    }
}
