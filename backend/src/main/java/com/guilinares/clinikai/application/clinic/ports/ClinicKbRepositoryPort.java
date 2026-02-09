package com.guilinares.clinikai.application.clinic.ports;

import com.guilinares.clinikai.domain.clinic.ClinicKbCategory;
import com.guilinares.clinikai.infrastructure.data.entities.ClinicKbEntryEntity;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClinicKbRepositoryPort {
    Optional<ClinicKbEntryEntity> findById(UUID kbId);
    Page<ClinicKbEntryEntity> findAllByClinicId(UUID clinicId, Boolean enabled, String category, String q,
                                                String[] tags, int pageNumber, int pageSize);
    ClinicKbEntryEntity save(ClinicKbEntryEntity entity);
    void delete(UUID kbId);
    void setEnabled(UUID kbId, Boolean enabled);
}
