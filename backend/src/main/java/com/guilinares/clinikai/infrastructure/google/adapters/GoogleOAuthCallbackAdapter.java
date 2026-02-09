package com.guilinares.clinikai.infrastructure.google.adapters;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.guilinares.clinikai.application.google.ports.GoogleCallback;
import com.guilinares.clinikai.infrastructure.data.entities.GoogleCalendarIntegrationEntity;
import com.guilinares.clinikai.infrastructure.data.repositories.GoogleCalendarIntegrationRepository;
import com.guilinares.clinikai.infrastructure.google.GoogleProperties;
import com.guilinares.clinikai.infrastructure.google.exceptions.GoogleCallbackException;
import com.guilinares.clinikai.infrastructure.security.crypto.CryptoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GoogleOAuthCallbackAdapter implements GoogleCallback {

    private final GoogleProperties props;
    private final GoogleCalendarIntegrationRepository repository;
    private final CryptoService cryptoService;

    @Override
    public void handle(String code, UUID clinicId) {
        try {
            GoogleTokenResponse tokenResponse =
                    new GoogleAuthorizationCodeTokenRequest(
                            new NetHttpTransport(),
                            GsonFactory.getDefaultInstance(),
                            "https://oauth2.googleapis.com/token",
                            props.clientId(),
                            props.clientSecret(),
                            code,
                            props.redirectUri()
                    ).execute();

            GoogleCalendarIntegrationEntity integration =
                    repository.findByClinicId(clinicId)
                            .orElse(GoogleCalendarIntegrationEntity.builder()
                                    .clinicId(clinicId)
                                    .build());

            integration.setRefreshTokenEncrypted(
                    cryptoService.encrypt(tokenResponse.getRefreshToken())
            );
            integration.setAccessTokenEncrypted(
                    cryptoService.encrypt(tokenResponse.getAccessToken())
            );
            integration.setAccessTokenExpiresAt(
                    Instant.now().plusSeconds(tokenResponse.getExpiresInSeconds())
            );
            integration.setConnectedAt(Instant.now());
            repository.save(integration);
        } catch (Exception e) {
            throw new GoogleCallbackException(e.getMessage());
        }
    }
}
