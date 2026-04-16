package com.guilinares.clinikai.infrastructure.data.repositories;

import com.guilinares.clinikai.domain.clinic.ClinicStatus;
import com.guilinares.clinikai.infrastructure.data.entities.ClinicEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ClinicRepository extends JpaRepository<ClinicEntity, UUID> {
    Optional<ClinicEntity> findByWhatsappNumber(String whatsappNumber);

    Optional<ClinicEntity> findByEmail(String email);

    @Modifying
    @Query("UPDATE ClinicEntity c SET c.flowConfig = :flowJson, c.updatedAt = current_timestamp WHERE c.id = :clinicId")
    void updateFlowConfig(@Param("clinicId") UUID clinicId, @Param("flowJson") String flowJson);

    @Modifying
    @Query("UPDATE ClinicEntity c SET c.flowPrompt = :flowPrompt, c.updatedAt = current_timestamp WHERE c.id = :clinicId")
    void updateFlowPrompt(@Param("clinicId") UUID clinicId, @Param("flowPrompt") String flowPrompt);

    @Modifying
    @Query("UPDATE ClinicEntity c SET c.status = :status, c.updatedAt = current_timestamp WHERE c.id = :clinicId")
    void updateStatus(@Param("clinicId") UUID clinicId, @Param("status") ClinicStatus status);
}
