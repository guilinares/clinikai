package com.guilinares.clinikai.infrastructure.data.adapters;

import com.guilinares.clinikai.application.clinic.ports.ClinicRepositoryPort;
import com.guilinares.clinikai.domain.clinic.Clinic;
import com.guilinares.clinikai.domain.clinic.ClinicStatus;
import com.guilinares.clinikai.infrastructure.data.entities.ClinicEntity;
import com.guilinares.clinikai.infrastructure.data.repositories.ClinicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static com.guilinares.clinikai.domain.clinic.Clinic.toDomain;
import static com.guilinares.clinikai.domain.clinic.Clinic.toEntity;

@Component
@RequiredArgsConstructor
public class ClinicRepositoryAdapter implements ClinicRepositoryPort {

    private final ClinicRepository repo;

    @Override
    public ClinicEntity save(Clinic clinic) {
        ClinicEntity entity = toEntity(clinic);
        return repo.save(entity);
    }

    @Override
    public Optional<ClinicEntity> findById(UUID clinicId) {
        return repo.findById(clinicId);
    }

    @Override
    public Optional<ClinicEntity> findByPhone(String phone) {
        return repo.findByWhatsappNumber(phone);
    }

    @Override
    public Optional<ClinicEntity> findByEmail(String email) {
        return repo.findByEmail(email);
    }

    @Override
    public ClinicEntity getReference(UUID clinicId) {
        return repo.getReferenceById(clinicId);
    }

    @Override
    @Transactional
    public void updateFlow(UUID clinicId, String flowJson) {
        repo.updateFlowConfig(clinicId, flowJson);
    }

    @Override
    @Transactional
    public void updateFlowPrompt(UUID clinicId, String flowPrompt) {
        repo.updateFlowPrompt(clinicId, flowPrompt);
    }

    @Override
    @Transactional
    public void updateStatus(UUID clinicId, ClinicStatus status) {
        repo.updateStatus(clinicId, status);
    }
}
