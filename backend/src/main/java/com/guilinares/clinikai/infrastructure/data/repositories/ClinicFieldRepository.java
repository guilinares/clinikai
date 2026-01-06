package com.guilinares.clinikai.infrastructure.data.repositories;

import com.guilinares.clinikai.infrastructure.data.entities.ClinicFieldEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClinicFieldRepository extends JpaRepository<ClinicFieldEntity, UUID> {
    List<ClinicFieldEntity> findByClinicIdAndEnabledTrueOrderByPriorityAsc(UUID clinicId);
    Optional<ClinicFieldEntity> findByClinicIdAndFieldKey(UUID clinicId, String fieldKey);
}
