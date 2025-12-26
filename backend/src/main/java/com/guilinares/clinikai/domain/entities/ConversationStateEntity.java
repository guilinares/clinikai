package com.guilinares.clinikai.domain.entities;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.time.OffsetDateTime;
import java.util.Map;
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

    @Type(JsonType.class)
    @Column(name = "state", columnDefinition = "jsonb", nullable = false)
    private Map<String, Object> state;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (state == null) state = Map.of();
        updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}
