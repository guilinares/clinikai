package com.guilinares.clinikai.application.whatsapp.ports;

import com.guilinares.clinikai.domain.enums.ClinicWhatsappProvider;
import com.guilinares.clinikai.infrastructure.data.entities.ClinicEntity;
import com.guilinares.clinikai.infrastructure.data.entities.ClinicWhatsappEntity;

import java.util.Optional;
import java.util.UUID;

public interface ClinicWhatsappRepositoryPort {

    Optional<ClinicWhatsappEntity> findByClinicId(UUID clinicId);
    Optional<ClinicWhatsappEntity> findByProviderAndInstanceId(ClinicWhatsappProvider provider, String instanceId);
    ClinicWhatsappEntity save(ClinicWhatsappEntity entity);
}
