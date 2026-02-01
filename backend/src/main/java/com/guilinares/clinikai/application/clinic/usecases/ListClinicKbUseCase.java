package com.guilinares.clinikai.application.clinic.usecases;

import com.guilinares.clinikai.application.clinic.exceptions.ClinicaNaoEncontradaException;
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

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class ListClinicKbUseCase {

    private final ClinicKbRepositoryPort clinicKb;
    private final ClinicRepositoryPort clinic;

    public PagedResponse<ClinicKbEntryResponse> execute(String clinicPhone, Boolean enabled, String category, Pageable pageable) {
        Optional<ClinicEntity> clinicEntity = clinic.findByPhone(clinicPhone);
        if (clinicEntity.isEmpty()) throw new ClinicaNaoEncontradaException("Clinica não encotrada");
        Page<ClinicKbEntryEntity> pagedKbs = clinicKb.findAllByClinicId(clinicEntity.get().getId(), enabled, ClinicKbCategory.nameFrom(category), null, pageable.getPageNumber(), pageable.getPageSize());
        if (pagedKbs.isEmpty()) throw new NotClinicKbFound();
        var items = pagedKbs.getContent().stream()
                .map(ClinicKbEntryResponse::fromEntity)
                .toList();
        return PagedResponse.from(pagedKbs, items);
    }

    public record ClinicKbEntryResponse(
            java.util.UUID id,
            String title,
            String content,
            String category,
            String[] tags,
            boolean enabled,
            java.time.OffsetDateTime createdAt,
            java.time.OffsetDateTime updatedAt
    ) {
        public static ClinicKbEntryResponse fromEntity(ClinicKbEntryEntity e) {
            return new ClinicKbEntryResponse(
                    e.getId(),
                    e.getTitle(),
                    e.getContent(),
                    e.getCategory().name(),
                    e.getTags(),
                    e.isEnabled(),
                    e.getCreatedAt(),
                    e.getUpdatedAt()
            );
        }
    }
}
