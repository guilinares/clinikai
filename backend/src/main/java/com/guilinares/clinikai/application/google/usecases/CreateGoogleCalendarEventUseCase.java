package com.guilinares.clinikai.application.google.usecases;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.UserCredentials;
import com.guilinares.clinikai.application.google.exceptions.FalhaAoAgendarException;
import com.guilinares.clinikai.infrastructure.data.entities.GoogleCalendarIntegrationEntity;
import com.guilinares.clinikai.infrastructure.data.repositories.GoogleCalendarIntegrationRepository;
import com.guilinares.clinikai.infrastructure.google.GoogleProperties;
import com.guilinares.clinikai.infrastructure.security.crypto.CryptoService;
import com.guilinares.clinikai.presentation.controllers.CalendarController;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.UUID;

@RequiredArgsConstructor
public class CreateGoogleCalendarEventUseCase {

    private final GoogleCalendarIntegrationRepository repository;
    private final CryptoService cryptoService;
    private final GoogleProperties props;

    public void execute(UUID clinicId, CalendarController.CalendarEventRequest request) {
        try {
            GoogleCalendarIntegrationEntity integration =
                    repository.findByClinicId(clinicId)
                            .orElseThrow(() -> new IllegalStateException("NOT_CONNECTED"));

            String accessToken = cryptoService.decrypt(
                    integration.getAccessTokenEncrypted()
            );
            String refreshToken = cryptoService.decrypt(
                    integration.getRefreshTokenEncrypted()
            );

            UserCredentials credentials = UserCredentials.newBuilder()
                    .setClientId(props.clientId())
                    .setClientSecret(props.clientSecret())
                    .setAccessToken(new AccessToken(
                            accessToken,
                            Date.from(integration.getAccessTokenExpiresAt())
                    ))
                    .setRefreshToken(refreshToken)
                    .build();


            Calendar calendar = new Calendar.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance(),
                    new HttpCredentialsAdapter(credentials)
            ).setApplicationName("ClinikAI").build();

            Event event = new Event()
                    .setSummary(request.title())
                    .setDescription(request.description())
                    .setStart(new EventDateTime()
                            .setDateTime(new com.google.api.client.util.DateTime(request.start())))
                    .setEnd(new EventDateTime()
                            .setDateTime(new com.google.api.client.util.DateTime(request.end())));

            String calendarId = integration.getCalendarId();
            if (calendarId == null || calendarId.isBlank()) {
                calendarId = "primary";
                integration.setCalendarId(calendarId);
                repository.save(integration);
            }

            calendar.events()
                    .insert(calendarId, event)
                    .execute();
        } catch (Exception e) {
            throw new FalhaAoAgendarException(e.getMessage());
        }
    }
}
