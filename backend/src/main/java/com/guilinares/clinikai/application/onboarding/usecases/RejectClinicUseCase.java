package com.guilinares.clinikai.application.onboarding.usecases;

import com.guilinares.clinikai.application.clinic.ports.ClinicRepositoryPort;
import com.guilinares.clinikai.domain.clinic.ClinicStatus;
import com.guilinares.clinikai.infrastructure.data.entities.ClinicEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
public class RejectClinicUseCase {

    private final ClinicRepositoryPort clinics;

    @Transactional
    public void execute(UUID clinicId) {
        ClinicEntity entity = clinics.findById(clinicId)
                .orElseThrow(() -> new IllegalArgumentException("Clínica não encontrada."));

        if (entity.getStatus() != ClinicStatus.PENDING_APPROVAL) {
            throw new IllegalStateException("Clínica não está pendente de aprovação.");
        }

        clinics.updateStatus(clinicId, ClinicStatus.REJECTED);
    }
}
