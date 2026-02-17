package com.guilinares.clinikai.application.google;

import com.guilinares.clinikai.application.google.usecases.CreateGoogleCalendarEventUseCase;
import com.guilinares.clinikai.application.google.usecases.GoogleConnectionStatusUseCase;
import com.guilinares.clinikai.infrastructure.data.repositories.GoogleCalendarIntegrationRepository;
import com.guilinares.clinikai.infrastructure.google.GoogleProperties;
import com.guilinares.clinikai.infrastructure.security.crypto.CryptoService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GoogleUsecaseConfig {

    @Bean
    public CreateGoogleCalendarEventUseCase createGoogleCalendarEventUseCase(GoogleCalendarIntegrationRepository repository,
                                                                             CryptoService cryptoService, GoogleProperties properties) {
        return new CreateGoogleCalendarEventUseCase(repository, cryptoService, properties);
    }

    @Bean
    public GoogleConnectionStatusUseCase googleConnectionStatusUseCase(GoogleCalendarIntegrationRepository repository) {
        return new GoogleConnectionStatusUseCase(repository);
    }
}
