package com.guilinares.clinikai.application.google;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.UserCredentials;
import com.guilinares.clinikai.infrastructure.data.entities.GoogleCalendarIntegrationEntity;
import com.guilinares.clinikai.infrastructure.google.GoogleProperties;
import com.guilinares.clinikai.infrastructure.security.crypto.CryptoService;

import java.util.Date;

public class GoogleCalendarClientFactory {

    public static Calendar build(GoogleCalendarIntegrationEntity integration,
                                 CryptoService cryptoService,
                                 GoogleProperties props) {
        try {
            String accessToken = cryptoService.decrypt(integration.getAccessTokenEncrypted());
            String refreshToken = cryptoService.decrypt(integration.getRefreshTokenEncrypted());

            UserCredentials credentials = UserCredentials.newBuilder()
                    .setClientId(props.clientId())
                    .setClientSecret(props.clientSecret())
                    .setAccessToken(new AccessToken(accessToken, Date.from(integration.getAccessTokenExpiresAt())))
                    .setRefreshToken(refreshToken)
                    .build();

            return new Calendar.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance(),
                    new HttpCredentialsAdapter(credentials)
            ).setApplicationName("ClinikAI").build();
        } catch (Exception e) {
            throw new RuntimeException("Falha ao construir cliente Google Calendar", e);
        }
    }
}
