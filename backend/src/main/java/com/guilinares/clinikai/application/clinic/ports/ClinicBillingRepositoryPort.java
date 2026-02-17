package com.guilinares.clinikai.application.clinic.ports;

import com.guilinares.clinikai.infrastructure.data.entities.ClinicBillingEntity;
import com.guilinares.clinikai.infrastructure.data.entities.ClinicEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClinicBillingRepositoryPort {
    ClinicBillingEntity save(ClinicBillingEntity entity);
    ClinicBillingEntity createClinicBilling(ClinicEntity clinic);
    Optional<ClinicBillingEntity> findByClinidId(UUID clinicID);
    Optional<ClinicBillingEntity> findByAsaasSubscriptionId(String asaasSubscriptionId);
    List<ClinicBillingEntity> findAll();
}
