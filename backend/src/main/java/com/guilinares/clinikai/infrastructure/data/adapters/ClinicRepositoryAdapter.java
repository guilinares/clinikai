package com.guilinares.clinikai.infrastructure.data.adapters;

import com.guilinares.clinikai.application.clinic.ports.ClinicRepositoryPort;
import com.guilinares.clinikai.domain.clinic.Clinic;
import com.guilinares.clinikai.infrastructure.data.entities.ClinicEntity;
import com.guilinares.clinikai.infrastructure.data.repositories.ClinicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

import static com.guilinares.clinikai.domain.clinic.Clinic.toDomain;
import static com.guilinares.clinikai.domain.clinic.Clinic.toEntity;

@Component
@RequiredArgsConstructor
public class ClinicRepositoryAdapter implements ClinicRepositoryPort {

    private final ClinicRepository repo;

    @Override
    public Clinic save(Clinic clinic) {
        ClinicEntity entity = toEntity(clinic);
        ClinicEntity saved = repo.save(entity);
        return toDomain(saved);
    }

    @Override
    public Clinic findById(UUID clinicId) {
        Optional<ClinicEntity> clinic = repo.findById(clinicId);
        return clinic.map(Clinic::toDomain).orElse(null);
    }

    @Override
    public Optional<ClinicEntity> findByPhone(String phone) {
        Optional<ClinicEntity> clinic = repo.findByWhatsappNumber(phone);
        return clinic;
    }
}
