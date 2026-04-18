package com.guilinares.clinikai.infrastructure.data.adapters;

import com.guilinares.clinikai.application.billing.ports.BillingEventRepositoryPort;
import com.guilinares.clinikai.infrastructure.data.entities.BillingEventEntity;
import com.guilinares.clinikai.infrastructure.data.repositories.BillingEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BillingEventRepositoryAdapter implements BillingEventRepositoryPort {

    private final BillingEventRepository billingEventRepository;

    @Override
    public void save(BillingEventEntry entry) {
        billingEventRepository.save(BillingEventEntity.builder()
                .clinicId(entry.clinicId())
                .eventType(entry.eventType())
                .paymentId(entry.paymentId())
                .subscriptionId(entry.subscriptionId())
                .statusBefore(entry.statusBefore())
                .statusAfter(entry.statusAfter())
                .rawPayload(entry.rawPayload())
                .build());
    }
}
