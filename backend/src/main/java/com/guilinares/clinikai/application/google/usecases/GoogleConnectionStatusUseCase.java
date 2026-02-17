package com.guilinares.clinikai.application.google.usecases;

import com.guilinares.clinikai.infrastructure.data.entities.GoogleCalendarIntegrationEntity;
import com.guilinares.clinikai.infrastructure.data.repositories.GoogleCalendarIntegrationRepository;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@RequiredArgsConstructor
public class GoogleConnectionStatusUseCase {

    private final GoogleCalendarIntegrationRepository repository;

    public GoogleConnectionStatusResponse getStatus(UUID clinicId) {

        return repository.findByClinicId(clinicId)
                .map(this::toResponse)
                .orElse(new GoogleConnectionStatusResponse(
                        false, false, false, null, null, null
                ));
    }

    private GoogleConnectionStatusResponse toResponse(GoogleCalendarIntegrationEntity it) {
        boolean connected = it.getConnectedAt() != null && it.getRevokedAt() == null;
        boolean active = it.isActive();
        boolean needsReconnect = it.getRevokedAt() != null;

        return new GoogleConnectionStatusResponse(
                connected,
                active,
                needsReconnect,
                it.getCalendarId(),
                it.getConnectedAt(),
                it.getRevokedAt()
        );
    }

    public record GoogleConnectionStatusResponse(
            boolean connected,
            boolean active,
            boolean needsReconnect,
            String calendarId,
            Instant connectedAt,
            Instant revokedAt
    ) {}
}
