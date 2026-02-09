package com.guilinares.clinikai.application.clinic.usecases;

import com.guilinares.clinikai.application.clinic.exceptions.ClinicaNaoEncontradaException;
import com.guilinares.clinikai.application.clinic.exceptions.KbNaoEncontradoException;
import com.guilinares.clinikai.application.clinic.exceptions.NotClinicKbFound;
import com.guilinares.clinikai.application.clinic.ports.ClinicKbRepositoryPort;
import com.guilinares.clinikai.application.clinic.ports.ClinicRepositoryPort;
import com.guilinares.clinikai.domain.clinic.ClinicKbCategory;
import com.guilinares.clinikai.infrastructure.data.entities.ClinicEntity;
import com.guilinares.clinikai.infrastructure.data.entities.ClinicKbEntryEntity;
import com.guilinares.clinikai.infrastructure.pagination.PagedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
