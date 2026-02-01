package com.guilinares.clinikai.application.clinic.ports;

import com.guilinares.clinikai.domain.clinic.Clinic;
import com.guilinares.clinikai.infrastructure.data.entities.ClinicEntity;

import java.util.Optional;
import java.util.UUID;

public interface ClinicRepositoryPort {

    Clinic save(Clinic clinic);
    Clinic findById(UUID clinicId);
    Optional<ClinicEntity> findByPhone(String phone);
    ClinicEntity getReference(UUID clinicId);
}
