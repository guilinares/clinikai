package com.guilinares.clinikai.infrastructure.data.entities;

import com.guilinares.clinikai.domain.enums.BillingPlan;
import com.guilinares.clinikai.domain.enums.BillingProvider;
import com.guilinares.clinikai.domain.enums.BillingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "clinic_billing")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClinicBillingEntity {

    @Id
    @Column(name = "clinic_id")
    private UUID clinicId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BillingProvider provider;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BillingPlan plan;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private BillingStatus status;

    @Column(name = "asaas_customer_id", length = 60)
    private String asaasCustomerId;

    @Column(name = "asaas_subscription_id", length = 60)
    private String asaasSubscriptionId;

    @Column(name = "last_payment_id", length = 60)
    private String lastPaymentId;

    // Cache opcional do Pix
    @Column(name = "last_pix_payload", columnDefinition = "text")
    private String lastPixPayload;

    @Column(name = "last_pix_encoded_image", columnDefinition = "text")
    private String lastPixEncodedImage;

    @Column(name = "last_pix_expires_at")
    private OffsetDateTime lastPixExpiresAt;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    void prePersist() {
        var now = OffsetDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;

        if (provider == null) provider = BillingProvider.ASAAS;
        if (plan == null) plan = BillingPlan.BASIC;
        if (status == null) status = BillingStatus.NO_SUBSCRIPTION;
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }
}
