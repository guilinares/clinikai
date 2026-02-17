package com.guilinares.clinikai.application.clinic.usecases;

import com.guilinares.clinikai.application.clinic.exceptions.KbNaoEncontradoException;
import com.guilinares.clinikai.application.clinic.ports.ClinicKbRepositoryPort;
import com.guilinares.clinikai.application.clinic.ports.ClinicRepositoryPort;
import com.guilinares.clinikai.domain.enums.ClinicKbCategory;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class EditClinicKbUseCase {

    private final ClinicKbRepositoryPort clinicKb;
    private final ClinicRepositoryPort clinic;

    public ListClinicKbUseCase.ClinicKbEntryResponse execute(
            String kbId,
            String clinicId,
            String title,
            String content,
            String category,
            boolean enabled
    ) {
        UUID kbUuid = UUID.fromString(kbId);
        UUID clinicUuid = UUID.fromString(clinicId);

        var kbEntity = clinicKb.findById(kbUuid)
                .orElseThrow(() -> new KbNaoEncontradoException("KB não encontrado"));

        kbEntity.setTitle(title);
        kbEntity.setContent(content);
        kbEntity.setCategory(ClinicKbCategory.from(category));
        kbEntity.setEnabled(enabled);

        var saved = clinicKb.save(kbEntity);

        return ListClinicKbUseCase.ClinicKbEntryResponse.fromEntity(saved);
    }
}
