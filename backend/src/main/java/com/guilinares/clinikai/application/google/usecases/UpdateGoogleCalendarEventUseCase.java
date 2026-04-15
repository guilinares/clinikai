package com.guilinares.clinikai.application.google.usecases;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.guilinares.clinikai.application.google.GoogleCalendarClientFactory;
import com.guilinares.clinikai.application.google.exceptions.EventoNaoEncontradoException;
import com.guilinares.clinikai.application.google.exceptions.FalhaAoAgendarException;
import com.guilinares.clinikai.application.google.exceptions.HorarioIndisponivelException;
import com.guilinares.clinikai.infrastructure.data.entities.GoogleCalendarIntegrationEntity;
import com.guilinares.clinikai.infrastructure.data.repositories.GoogleCalendarIntegrationRepository;
import com.guilinares.clinikai.infrastructure.google.GoogleProperties;
import com.guilinares.clinikai.infrastructure.security.crypto.CryptoService;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class UpdateGoogleCalendarEventUseCase {

    private final GoogleCalendarIntegrationRepository repository;
    private final CryptoService cryptoService;
    private final GoogleProperties props;

    public record UpdateEventRequest(String title, String description, String start, String end) {}

    public void execute(UUID clinicId, String eventId, UpdateEventRequest request) {
        try {
            GoogleCalendarIntegrationEntity integration = repository.findByClinicId(clinicId)
                    .orElseThrow(() -> new IllegalStateException("NOT_CONNECTED"));

            Calendar calendar = GoogleCalendarClientFactory.build(integration, cryptoService, props);
            String calendarId = integration.getCalendarId() != null ? integration.getCalendarId() : "primary";

            Event existing;
            try {
                existing = calendar.events().get(calendarId, eventId).execute();
            } catch (Exception e) {
                throw new EventoNaoEncontradoException(eventId);
            }

            // verifica conflitos de horário se as datas mudaram
            boolean datesChanged =
                    !request.start().equals(existing.getStart().getDateTime() != null
                            ? existing.getStart().getDateTime().toStringRfc3339() : "")
                    || !request.end().equals(existing.getEnd().getDateTime() != null
                            ? existing.getEnd().getDateTime().toStringRfc3339() : "");

            if (datesChanged) {
                var events = calendar.events().list(calendarId)
                        .setTimeMin(new DateTime(request.start()))
                        .setTimeMax(new DateTime(request.end()))
                        .setSingleEvents(true)
                        .execute();

                boolean conflict = events.getItems().stream()
                        .filter(e -> !"cancelled".equals(e.getStatus()))
                        .anyMatch(e -> !e.getId().equals(eventId));

                if (conflict) throw new HorarioIndisponivelException();
            }

            existing.setSummary(request.title());
            existing.setDescription(request.description());
            existing.setStart(new EventDateTime().setDateTime(new DateTime(request.start())));
            existing.setEnd(new EventDateTime().setDateTime(new DateTime(request.end())));

            calendar.events().update(calendarId, eventId, existing).execute();
        } catch (HorarioIndisponivelException | EventoNaoEncontradoException | IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new FalhaAoAgendarException(e.getMessage());
        }
    }
}
