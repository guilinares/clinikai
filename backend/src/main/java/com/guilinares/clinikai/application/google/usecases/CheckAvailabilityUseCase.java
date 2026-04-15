package com.guilinares.clinikai.application.google.usecases;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.guilinares.clinikai.application.google.GoogleCalendarClientFactory;
import com.guilinares.clinikai.application.google.exceptions.FalhaAoAgendarException;
import com.guilinares.clinikai.infrastructure.data.entities.GoogleCalendarIntegrationEntity;
import com.guilinares.clinikai.infrastructure.data.repositories.GoogleCalendarIntegrationRepository;
import com.guilinares.clinikai.infrastructure.google.GoogleProperties;
import com.guilinares.clinikai.infrastructure.security.crypto.CryptoService;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class CheckAvailabilityUseCase {

    private final GoogleCalendarIntegrationRepository repository;
    private final CryptoService cryptoService;
    private final GoogleProperties props;

    public record AvailabilityResponse(boolean available, List<OccupiedSlot> occupiedSlots) {}
    public record OccupiedSlot(String eventId, String title, String start, String end) {}

    /**
     * Verifica disponibilidade entre {@code start} e {@code end} (formato RFC3339).
     * Retorna se o período está livre e lista eventos conflitantes caso não esteja.
     */
    public AvailabilityResponse execute(UUID clinicId, String start, String end) {
        try {
            GoogleCalendarIntegrationEntity integration = repository.findByClinicId(clinicId)
                    .orElseThrow(() -> new IllegalStateException("NOT_CONNECTED"));

            Calendar calendar = GoogleCalendarClientFactory.build(integration, cryptoService, props);
            String calendarId = integration.getCalendarId() != null ? integration.getCalendarId() : "primary";

            Events events = calendar.events().list(calendarId)
                    .setTimeMin(new DateTime(start))
                    .setTimeMax(new DateTime(end))
                    .setSingleEvents(true)
                    .setOrderBy("startTime")
                    .execute();

            List<OccupiedSlot> occupied = events.getItems().stream()
                    .filter(e -> !"cancelled".equals(e.getStatus()))
                    .map(e -> new OccupiedSlot(
                            e.getId(),
                            e.getSummary(),
                            e.getStart().getDateTime() != null ? e.getStart().getDateTime().toStringRfc3339() : e.getStart().getDate().toStringRfc3339(),
                            e.getEnd().getDateTime() != null ? e.getEnd().getDateTime().toStringRfc3339() : e.getEnd().getDate().toStringRfc3339()
                    ))
                    .toList();

            return new AvailabilityResponse(occupied.isEmpty(), occupied);
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new FalhaAoAgendarException(e.getMessage());
        }
    }
}
