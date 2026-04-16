package com.guilinares.clinikai.application.clinic.ports;

import com.guilinares.clinikai.domain.clinic.Clinic;
import com.guilinares.clinikai.domain.clinic.ClinicStatus;
import com.guilinares.clinikai.infrastructure.data.entities.ClinicEntity;

import java.util.Optional;
import java.util.UUID;

public interface ClinicRepositoryPort {

    ClinicEntity save(Clinic clinic);
    Optional<ClinicEntity> findById(UUID clinicId);
    Optional<ClinicEntity> findByPhone(String phone);
    Optional<ClinicEntity> findByEmail(String email);
    ClinicEntity getReference(UUID clinicId);
    void updateFlow(UUID clinicId, String flowJson);
    void updateFlowPrompt(UUID clinicId, String flowPrompt);
    void updateStatus(UUID clinicId, ClinicStatus status);
}
