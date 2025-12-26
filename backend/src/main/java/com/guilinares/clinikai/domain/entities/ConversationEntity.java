package com.guilinares.clinikai.domain.entities;

import com.guilinares.clinikai.domain.enums.Channel;
import com.guilinares.clinikai.domain.enums.ConversationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "conversations",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_conversations_open",
                        columnNames = {"clinic_id", "patient_id", "channel", "status"}
                )
        }
)
@Getter @Setter
@Builder
@NoArgsConstructor @AllArgsConstructor
public class ConversationEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "clinic_id", nullable = false)
    private ClinicEntity clinic;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private PatientEntity patient;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Channel channel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ConversationStatus status;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        var now = OffsetDateTime.now();
        createdAt = now;
        updatedAt = now;
        if (status == null) status = ConversationStatus.OPEN;
        if (channel == null) channel = Channel.WHATSAPP;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}
