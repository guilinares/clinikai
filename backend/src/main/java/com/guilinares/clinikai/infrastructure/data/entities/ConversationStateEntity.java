package com.guilinares.clinikai.infrastructure.data.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "conversation_state")
@Getter @Setter
@Builder
@NoArgsConstructor @AllArgsConstructor
public class ConversationStateEntity {

    @Id
    @Column(name = "conversation_id")
    private UUID conversationId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "conversation_id")
    private ConversationEntity conversation;

    @Column(name = "state")
    private String state;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (state == null) state = "";
        updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}
