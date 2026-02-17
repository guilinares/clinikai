package com.guilinares.clinikai.infrastructure.data.entities;

import com.guilinares.clinikai.domain.enums.ClinicWhatsappProvider;
import com.guilinares.clinikai.domain.enums.ClinicWhatsappStatus;
import com.guilinares.clinikai.infrastructure.data.entities.ClinicEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "clinic_whatsapp",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_clinic_whatsapp_clinic", columnNames = {"clinic_id"}),
                @UniqueConstraint(name = "uq_clinic_whatsapp_instance", columnNames = {"provider", "instance_id"})
        },
        indexes = {
                @Index(name = "idx_clinic_whatsapp_clinic_id", columnList = "clinic_id"),
                @Index(name = "idx_clinic_whatsapp_status", columnList = "status"),
                @Index(name = "idx_clinic_whatsapp_is_shared", columnList = "is_shared")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClinicWhatsappEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    // 1 WhatsApp por clínica
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "clinic_id", nullable = false, foreignKey = @ForeignKey(name = "fk_clinic_whatsapp_clinic"))
    private ClinicEntity clinic;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false, length = 30)
    private ClinicWhatsappProvider provider = ClinicWhatsappProvider.ZAPI;

    @Column(name = "instance_id", nullable = false, length = 120)
    private String instanceId;

    @Column(name = "instance_token", nullable = false, columnDefinition = "text")
    private String instanceToken;

    @Column(name = "base_url", nullable = false, columnDefinition = "text")
    private String baseUrl;

    @Column(name = "webhook_url", columnDefinition = "text")
    private String webhookUrl;

    @Column(name = "webhook_secret", columnDefinition = "text")
    private String webhookSecret;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private ClinicWhatsappStatus status = ClinicWhatsappStatus.UNPROVISIONED;

    @Column(name = "is_shared", nullable = false)
    private boolean shared = false;

    @Column(name = "phone_e164", length = 20)
    private String phoneE164;

    @Column(name = "last_connection_at")
    private OffsetDateTime lastConnectionAt;

    @Column(name = "last_error_code", length = 60)
    private String lastErrorCode;

    @Column(name = "last_error_message", columnDefinition = "text")
    private String lastErrorMessage;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    void prePersist() {
        if (id == null) id = UUID.randomUUID();
        var now = OffsetDateTime.now();
        if (createdAt == null) createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}
