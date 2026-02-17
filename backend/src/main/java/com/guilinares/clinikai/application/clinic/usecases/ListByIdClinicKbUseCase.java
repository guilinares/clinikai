package com.guilinares.clinikai.application.clinic.usecases;

import com.guilinares.clinikai.application.clinic.exceptions.KbNaoEncontradoException;
import com.guilinares.clinikai.application.clinic.ports.ClinicKbRepositoryPort;
import com.guilinares.clinikai.infrastructure.data.entities.ClinicKbEntryEntity;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class ListByIdClinicKbUseCase {

    private final ClinicKbRepositoryPort clinicKb;

    public ListClinicKbUseCase.ClinicKbEntryResponse execute(String kbId) {
        Optional<ClinicKbEntryEntity> clinicEntity = clinicKb.findById(UUID.fromString(kbId));
        if (clinicEntity.isEmpty()) throw new KbNaoEncontradoException("Kb não encontrado.");
        return ListClinicKbUseCase.ClinicKbEntryResponse.fromEntity(clinicEntity.get());
    }
}
