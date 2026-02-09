package com.guilinares.clinikai.infrastructure.data.repositories;

import com.guilinares.clinikai.infrastructure.data.entities.GoogleCalendarIntegrationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface GoogleCalendarIntegrationRepository extends JpaRepository<GoogleCalendarIntegrationEntity, UUID> {
    Optional<GoogleCalendarIntegrationEntity> findByClinicId(UUID clinicId);
}
