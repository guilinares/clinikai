package com.guilinares.clinikai.infrastructure.data.adapters;

import com.guilinares.clinikai.application.clinic.ports.ClinicKbRepositoryPort;
import com.guilinares.clinikai.domain.clinic.ClinicKbCategory;
import com.guilinares.clinikai.infrastructure.data.entities.ClinicKbEntryEntity;
import com.guilinares.clinikai.infrastructure.data.repositories.ClinicKbEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ClinicKbRespositoryAdapter implements ClinicKbRepositoryPort {

    private final ClinicKbEntryRepository clinicKbEntryRepository;

    @Override
    public Optional<ClinicKbEntryEntity> findById(UUID kbId) {
        return clinicKbEntryRepository.findById(kbId);
    }

    @Override
    public Page<ClinicKbEntryEntity> findAllByClinicId(UUID clinicId, Boolean enabled, String category, String q,
                                                       String[] tags, int pageNumber, int pageSize) {
        return clinicKbEntryRepository.listByClinic(clinicId,
                enabled, category, q, null, PageRequest.of(pageNumber, pageSize));
    }

    @Override
    public ClinicKbEntryEntity save(ClinicKbEntryEntity entity) {
        return clinicKbEntryRepository.save(entity);
    }

    @Override
    public void delete(UUID kbId) {
        clinicKbEntryRepository.deleteById(kbId);
    }

    @Override
    public void setEnabled(UUID kbId, Boolean enabled) {
        clinicKbEntryRepository.setEnabled(kbId, enabled, OffsetDateTime.now());
    }
}
