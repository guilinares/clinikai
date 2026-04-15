package com.guilinares.clinikai.infrastructure.data.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.time.OffsetDateTime;
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

    @Column(name = "email", length = 70)
    private String email;

    @Column(name = "billing_document", length = 14)
    private String billingDocument;

    @Column(length = 50, nullable = false)
    private String timezone;

    @Column(name = "ai_config")
    private String aiConfig;

    @Column(name = "flow_config", columnDefinition = "text")
    private String flowConfig;

    @Column(name = "flow_prompt", columnDefinition = "text")
    private String flowPrompt;

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
        if (aiConfig == null) aiConfig = "";
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}