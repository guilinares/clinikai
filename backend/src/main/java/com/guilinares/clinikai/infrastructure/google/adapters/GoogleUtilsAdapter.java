package com.guilinares.clinikai.infrastructure.google.adapters;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.guilinares.clinikai.application.google.ports.GoogleUtils;
import com.guilinares.clinikai.infrastructure.google.GoogleProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GoogleUtilsAdapter implements GoogleUtils {

    private final GoogleProperties props;

    @Override
    public String buildAuthorizationUrl(UUID clinicId) {
        return new GoogleAuthorizationCodeRequestUrl(
                props.clientId(),
                props.redirectUri(),
                List.of("https://www.googleapis.com/auth/calendar.events")
        )
                .setAccessType("offline")
                .set("prompt", "consent")
                .setState(clinicId.toString())
                .build();    }
}
