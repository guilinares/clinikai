package com.guilinares.clinikai.infrastructure.data.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "clinic_fields",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_clinic_fields",
                        columnNames = {"clinic_id", "field_key"}
                )
        }
)
@Getter @Setter
@Builder
@NoArgsConstructor @AllArgsConstructor
public class ClinicFieldEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "clinic_id", nullable = false)
    private ClinicEntity clinic;

    @Column(name = "field_key", nullable = false, length = 50)
    private String fieldKey;

    @Column(nullable = false)
    private boolean required;

    @Column(name = "prompt_question", nullable = false, columnDefinition = "text")
    private String promptQuestion;

    @Column(nullable = false)
    private int priority;

    @Column(name = "extractor_hint", columnDefinition = "text")
    private String extractorHint;

    @Column(nullable = false)
    private boolean enabled;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        var now = OffsetDateTime.now();
        createdAt = now;
        updatedAt = now;
        if (priority == 0) priority = 100;
        if (!enabled) enabled = true;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}