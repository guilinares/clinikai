package com.guilinares.clinikai.infrastructure.data.repositories;

import com.guilinares.clinikai.infrastructure.data.entities.ClinicKbEntryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.List;

public interface ClinicKbEntryRepository extends JpaRepository<ClinicKbEntryEntity, UUID> {
    List<ClinicKbEntryEntity> findByClinicIdAndEnabledTrue(UUID clinicId);
}
