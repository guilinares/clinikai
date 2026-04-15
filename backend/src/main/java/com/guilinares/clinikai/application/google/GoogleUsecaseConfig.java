package com.guilinares.clinikai.application.google;

import com.guilinares.clinikai.application.google.usecases.*;
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

    @Bean
    public CheckAvailabilityUseCase checkAvailabilityUseCase(GoogleCalendarIntegrationRepository repository,
                                                             CryptoService cryptoService, GoogleProperties properties) {
        return new CheckAvailabilityUseCase(repository, cryptoService, properties);
    }

    @Bean
    public RescheduleGoogleCalendarEventUseCase rescheduleGoogleCalendarEventUseCase(GoogleCalendarIntegrationRepository repository,
                                                                                     CryptoService cryptoService, GoogleProperties properties) {
        return new RescheduleGoogleCalendarEventUseCase(repository, cryptoService, properties);
    }

    @Bean
    public CancelGoogleCalendarEventUseCase cancelGoogleCalendarEventUseCase(GoogleCalendarIntegrationRepository repository,
                                                                             CryptoService cryptoService, GoogleProperties properties) {
        return new CancelGoogleCalendarEventUseCase(repository, cryptoService, properties);
    }

    @Bean
    public ListCalendarEventsUseCase listCalendarEventsUseCase(GoogleCalendarIntegrationRepository repository,
                                                               CryptoService cryptoService, GoogleProperties properties) {
        return new ListCalendarEventsUseCase(repository, cryptoService, properties);
    }

    @Bean
    public UpdateGoogleCalendarEventUseCase updateGoogleCalendarEventUseCase(GoogleCalendarIntegrationRepository repository,
                                                                             CryptoService cryptoService, GoogleProperties properties) {
        return new UpdateGoogleCalendarEventUseCase(repository, cryptoService, properties);
    }
}
