package com.guilinares.clinikai.application.patient.ports;

import com.guilinares.clinikai.domain.patient.Patient;
import com.guilinares.clinikai.infrastructure.data.entities.ClinicEntity;
import com.guilinares.clinikai.infrastructure.data.entities.PatientEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

public interface PatientRepositoryPort {
    Optional<PatientEntity> findByClinicIdAndPhone(UUID clinicId, String phoneId);
    PatientEntity save(ClinicEntity clinic, String phoneId);
    PatientEntity getReference(UUID patientId);
}
