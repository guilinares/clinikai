package com.guilinares.clinikai.infrastructure.data.adapters;

import com.guilinares.clinikai.application.patient.ports.PatientRepositoryPort;
import com.guilinares.clinikai.domain.patient.Patient;
import com.guilinares.clinikai.infrastructure.data.entities.ClinicEntity;
import com.guilinares.clinikai.infrastructure.data.entities.PatientEntity;
import com.guilinares.clinikai.infrastructure.data.repositories.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PatientRepositoryAdapter implements PatientRepositoryPort {

    private final PatientRepository patients;

    @Override
    public Optional<PatientEntity> findByClinicIdAndPhone(UUID clinicId, String phoneId) {
        return patients.findByClinicIdAndPhone(clinicId, phoneId);
    }

    @Override
    public PatientEntity save(ClinicEntity clinic, String phoneId) {
        PatientEntity patientEntity = PatientEntity.builder()
                .clinic(clinic)
                .phone(phoneId)
                .firstContactAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();
        return patients.save(patientEntity);
    }
}
