package com.guilinares.clinikai.infrastructure.data.repositories;

import com.guilinares.clinikai.infrastructure.data.entities.ClinicBillingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ClinicBillingRepository extends JpaRepository<ClinicBillingEntity, UUID> {

    @Query("select b.status from ClinicBillingEntity b where b.clinicId = :clinicId")
    Optional<String> findStatusByClinicId(@Param("clinicId") UUID clinicId);
}