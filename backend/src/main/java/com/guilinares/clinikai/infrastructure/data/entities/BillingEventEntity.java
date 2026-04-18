package com.guilinares.clinikai.infrastructure.data.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "billing_events")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingEventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "clinic_id", nullable = false)
    private UUID clinicId;

    @Column(name = "event_type", nullable = false, length = 60)
    private String eventType;

    @Column(name = "payment_id", length = 60)
    private String paymentId;

    @Column(name = "subscription_id", length = 60)
    private String subscriptionId;

    @Column(name = "status_before", length = 30)
    private String statusBefore;

    @Column(name = "status_after", length = 30)
    private String statusAfter;

    @Column(name = "raw_payload", columnDefinition = "text")
    private String rawPayload;

    @Column(name = "received_at", nullable = false)
    private OffsetDateTime receivedAt;

    @PrePersist
    void prePersist() {
        if (receivedAt == null) receivedAt = OffsetDateTime.now();
    }
}
