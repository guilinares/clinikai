package com.guilinares.clinikai.domain.entities;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;


@Entity
@Table(name = "clinics")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClinicEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 150)
    private String specialty;

    @Column(name = "whatsapp_number", length = 30)
    private String whatsappNumber;

    @Column(length = 50, nullable = false)
    private String timezone;

    @Type(JsonType.class)
    @Column(name = "ai_config", columnDefinition = "jsonb", nullable = false)
    private Map<String, Object> aiConfig;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        var now = OffsetDateTime.now();
        createdAt = now;
        updatedAt = now;
        if (timezone == null) timezone = "America/Sao_Paulo";
        if (aiConfig == null) aiConfig = Map.of();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}