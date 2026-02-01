package com.guilinares.clinikai.application.clinic.usecases;

import com.guilinares.clinikai.application.clinic.exceptions.ClinicaNaoEncontradaException;
import com.guilinares.clinikai.application.clinic.ports.ClinicKbRepositoryPort;
import com.guilinares.clinikai.application.clinic.ports.ClinicRepositoryPort;
import com.guilinares.clinikai.domain.clinic.ClinicKbCategory;
import com.guilinares.clinikai.infrastructure.data.entities.ClinicEntity;
import com.guilinares.clinikai.infrastructure.data.entities.ClinicKbEntryEntity;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class RegisterClinicKbUseCase {

    private final ClinicKbRepositoryPort clinicKb;
    private final ClinicRepositoryPort clinic;

    public ListClinicKbUseCase.ClinicKbEntryResponse execute(String clinicPhone, String title, String content, String category) {
        Optional<ClinicEntity> clinicEntity = clinic.findByPhone(clinicPhone);
        if (clinicEntity.isEmpty()) throw new ClinicaNaoEncontradaException("Clinica não encotrada");
        ClinicKbEntryEntity clinicKbEntity = ClinicKbEntryEntity.builder()
                .clinic(clinicEntity.get())
                .title(title)
                .content(content)
                .category(ClinicKbCategory.from(category))
                .tags(null)
                .build();
        ClinicKbEntryEntity entity = clinicKb.save(clinicKbEntity);
        return ListClinicKbUseCase.ClinicKbEntryResponse.fromEntity(entity);
    }
}
