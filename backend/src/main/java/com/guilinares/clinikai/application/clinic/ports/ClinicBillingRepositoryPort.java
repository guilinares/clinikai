package com.guilinares.clinikai.application.clinic.ports;

import com.guilinares.clinikai.infrastructure.data.entities.ClinicBillingEntity;
import com.guilinares.clinikai.infrastructure.data.entities.ClinicEntity;

public interface ClinicBillingRepositoryPort {
    ClinicBillingEntity createClinicBilling(ClinicEntity clinic);
}
