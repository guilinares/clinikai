package com.guilinares.clinikai.application.billing.ports;

import java.util.UUID;

public interface BillingEventRepositoryPort {

    void save(BillingEventEntry entry);

    record BillingEventEntry(
            UUID clinicId,
            String eventType,
            String paymentId,
            String subscriptionId,
            String statusBefore,
            String statusAfter,
            String rawPayload
    ) {}
}
