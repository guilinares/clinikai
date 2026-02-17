package com.guilinares.clinikai.infrastructure.data.adapters;

import com.guilinares.clinikai.application.whatsapp.ports.ClinicWhatsappRepositoryPort;
import com.guilinares.clinikai.domain.enums.ClinicWhatsappProvider;
import com.guilinares.clinikai.infrastructure.data.entities.ClinicEntity;
import com.guilinares.clinikai.infrastructure.data.entities.ClinicWhatsappEntity;
import com.guilinares.clinikai.infrastructure.data.repositories.ClinicWhatsappRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ClinicWhatsappRepositoryAdapter implements ClinicWhatsappRepositoryPort {

    private final ClinicWhatsappRepository clinicWhatsappRepository;

    @Override
    public Optional<ClinicWhatsappEntity> findByClinicId(UUID clinicId) {
        return clinicWhatsappRepository.findByClinicId(clinicId);
    }

    @Override
    public Optional<ClinicWhatsappEntity> findByProviderAndInstanceId(ClinicWhatsappProvider provider, String instanceId) {
        return clinicWhatsappRepository.findByProviderAndInstanceId(provider, instanceId);
    }

    @Override
    public ClinicWhatsappEntity save(ClinicWhatsappEntity entity) {
        return clinicWhatsappRepository.save(entity);
    }
}
