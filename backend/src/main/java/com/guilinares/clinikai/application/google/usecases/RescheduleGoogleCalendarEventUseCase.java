package com.guilinares.clinikai.application.google.usecases;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import com.guilinares.clinikai.application.google.GoogleCalendarClientFactory;
import com.guilinares.clinikai.application.google.exceptions.EventoNaoEncontradoException;
import com.guilinares.clinikai.application.google.exceptions.FalhaAoAgendarException;
import com.guilinares.clinikai.application.google.exceptions.HorarioIndisponivelException;
import com.guilinares.clinikai.infrastructure.data.entities.GoogleCalendarIntegrationEntity;
import com.guilinares.clinikai.infrastructure.data.repositories.GoogleCalendarIntegrationRepository;
import com.guilinares.clinikai.infrastructure.google.GoogleProperties;
import com.guilinares.clinikai.infrastructure.security.crypto.CryptoService;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class RescheduleGoogleCalendarEventUseCase {

    private final GoogleCalendarIntegrationRepository repository;
    private final CryptoService cryptoService;
    private final GoogleProperties props;

    /**
     * Reagenda o evento {@code eventId} para o novo intervalo {@code newStart}/{@code newEnd} (RFC3339).
     * Lança {@link HorarioIndisponivelException} se outro evento ocupar o horário.
     * Lança {@link EventoNaoEncontradoException} se o evento não existir.
     */
    public void execute(UUID clinicId, String eventId, String newStart, String newEnd) {
        try {
            GoogleCalendarIntegrationEntity integration = repository.findByClinicId(clinicId)
                    .orElseThrow(() -> new IllegalStateException("NOT_CONNECTED"));

            Calendar calendar = GoogleCalendarClientFactory.build(integration, cryptoService, props);
            String calendarId = integration.getCalendarId() != null ? integration.getCalendarId() : "primary";

            // Verifica conflitos excluindo o próprio evento
            Events events = calendar.events().list(calendarId)
                    .setTimeMin(new DateTime(newStart))
                    .setTimeMax(new DateTime(newEnd))
                    .setSingleEvents(true)
                    .execute();

            boolean conflict = events.getItems().stream()
                    .filter(e -> !"cancelled".equals(e.getStatus()))
                    .anyMatch(e -> !e.getId().equals(eventId));

            if (conflict) {
                throw new HorarioIndisponivelException();
            }

            Event existing;
            try {
                existing = calendar.events().get(calendarId, eventId).execute();
            } catch (Exception e) {
                throw new EventoNaoEncontradoException(eventId);
            }

            existing.setStart(new EventDateTime().setDateTime(new DateTime(newStart)));
            existing.setEnd(new EventDateTime().setDateTime(new DateTime(newEnd)));

            calendar.events().update(calendarId, eventId, existing).execute();
        } catch (HorarioIndisponivelException | EventoNaoEncontradoException | IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new FalhaAoAgendarException(e.getMessage());
        }
    }
}
