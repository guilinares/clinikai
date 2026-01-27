package com.guilinares.clinikai.infrastructure.data.repositories;

import com.guilinares.clinikai.infrastructure.data.entities.PatientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PatientRepository extends JpaRepository<PatientEntity, UUID> {
    Optional<PatientEntity> findByClinicIdAndPhone(UUID clinicId, String phone);
}
