package com.guilinares.clinicai.clinic;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ClinicRepository extends JpaRepository<ClinicEntity, UUID> {

    Optional<ClinicEntity> findByWhatsappNumber(String whatsappNumber);
}