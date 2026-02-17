package com.guilinares.clinikai.infrastructure.data.repositories;

import com.guilinares.clinikai.domain.enums.ClinicWhatsappProvider;
import com.guilinares.clinikai.infrastructure.data.entities.ClinicWhatsappEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ClinicWhatsappRepository extends JpaRepository<ClinicWhatsappEntity, UUID> {

    Optional<ClinicWhatsappEntity> findByClinicId(UUID clinicId);

    boolean existsByProviderAndInstanceId(ClinicWhatsappProvider provider, String instanceId);
    Optional<ClinicWhatsappEntity> findByProviderAndInstanceId(ClinicWhatsappProvider provider, String instanceId);

}
