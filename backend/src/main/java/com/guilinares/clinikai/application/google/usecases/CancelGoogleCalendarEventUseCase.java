package com.guilinares.clinikai.application.google.usecases;

import com.google.api.services.calendar.Calendar;
import com.guilinares.clinikai.application.google.GoogleCalendarClientFactory;
import com.guilinares.clinikai.application.google.exceptions.EventoNaoEncontradoException;
import com.guilinares.clinikai.application.google.exceptions.FalhaAoAgendarException;
import com.guilinares.clinikai.infrastructure.data.entities.GoogleCalendarIntegrationEntity;
import com.guilinares.clinikai.infrastructure.data.repositories.GoogleCalendarIntegrationRepository;
import com.guilinares.clinikai.infrastructure.google.GoogleProperties;
import com.guilinares.clinikai.infrastructure.security.crypto.CryptoService;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class CancelGoogleCalendarEventUseCase {

    private final GoogleCalendarIntegrationRepository repository;
    private final CryptoService cryptoService;
    private final GoogleProperties props;

    /**
     * Cancela (deleta) o evento {@code eventId} do calendário da clínica.
     * Lança {@link EventoNaoEncontradoException} se o evento não existir.
     */
    public void execute(UUID clinicId, String eventId) {
        try {
            GoogleCalendarIntegrationEntity integration = repository.findByClinicId(clinicId)
                    .orElseThrow(() -> new IllegalStateException("NOT_CONNECTED"));

            Calendar calendar = GoogleCalendarClientFactory.build(integration, cryptoService, props);
            String calendarId = integration.getCalendarId() != null ? integration.getCalendarId() : "primary";

            try {
                calendar.events().get(calendarId, eventId).execute();
            } catch (Exception e) {
                throw new EventoNaoEncontradoException(eventId);
            }

            calendar.events().delete(calendarId, eventId).execute();
        } catch (EventoNaoEncontradoException | IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new FalhaAoAgendarException(e.getMessage());
        }
    }
}
