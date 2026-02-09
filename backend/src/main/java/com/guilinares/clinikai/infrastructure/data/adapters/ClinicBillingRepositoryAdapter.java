package com.guilinares.clinikai.infrastructure.data.adapters;

import com.guilinares.clinikai.application.clinic.ports.ClinicBillingRepositoryPort;
import com.guilinares.clinikai.domain.enums.BillingPlan;
import com.guilinares.clinikai.domain.enums.BillingProvider;
import com.guilinares.clinikai.domain.enums.BillingStatus;
import com.guilinares.clinikai.infrastructure.data.entities.ClinicBillingEntity;
import com.guilinares.clinikai.infrastructure.data.entities.ClinicEntity;
import com.guilinares.clinikai.infrastructure.data.repositories.ClinicBillingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClinicBillingRepositoryAdapter implements ClinicBillingRepositoryPort {

    private final ClinicBillingRepository billingRepo;

    @Override
    public ClinicBillingEntity createClinicBilling(ClinicEntity clinic) {
        ClinicBillingEntity billing = ClinicBillingEntity.builder()
                .clinic(clinic)
                .clinicId(clinic.getId())
                .status(BillingStatus.NO_SUBSCRIPTION)
                .plan(BillingPlan.BASIC)
                .provider(BillingProvider.ASAAS)
                .build();
        return billingRepo.save(billing);
    }
}
