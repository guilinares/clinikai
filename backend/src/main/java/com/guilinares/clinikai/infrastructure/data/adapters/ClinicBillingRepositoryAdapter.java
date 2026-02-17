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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ClinicBillingRepositoryAdapter implements ClinicBillingRepositoryPort {

    private final ClinicBillingRepository billingRepo;

    @Override
    public ClinicBillingEntity save(ClinicBillingEntity entity) {
        return billingRepo.save(entity);
    }

    @Override
    public ClinicBillingEntity createClinicBilling(ClinicEntity clinic) {
        ClinicBillingEntity billing = ClinicBillingEntity.builder()
                .clinicId(clinic.getId())
                .status(BillingStatus.NO_SUBSCRIPTION)
                .plan(BillingPlan.BASIC)
                .provider(BillingProvider.ASAAS)
                .build();
        return billingRepo.save(billing);
    }

    @Override
    public Optional<ClinicBillingEntity> findByClinidId(UUID clinicID) {
        return billingRepo.findByClinicId(clinicID);
    }

    @Override
    public Optional<ClinicBillingEntity> findByAsaasSubscriptionId(String assasSubscriptionId) {
        return billingRepo.findByAsaasSubscriptionId(assasSubscriptionId);
    }

    @Override
    public List<ClinicBillingEntity> findAll() {
        return billingRepo.findAll();
    }

}
