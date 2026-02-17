package com.guilinares.clinikai.application.whatsapp;

import com.guilinares.clinikai.application.clinic.ports.ClinicRepositoryPort;
import com.guilinares.clinikai.application.whatsapp.ports.ClinicWhatsappRepositoryPort;
import com.guilinares.clinikai.application.whatsapp.usecases.ClinicWhatsappManualConfigUseCase;
import com.guilinares.clinikai.application.whatsapp.usecases.ClinicWhatsappProvisionUseCase;
import com.guilinares.clinikai.application.whatsapp.usecases.ClinicWhatsappQrCodeUseCase;
import com.guilinares.clinikai.application.whatsapp.usecases.ClinicWhatsappStatusUseCase;
import com.guilinares.clinikai.infrastructure.whatsapp.clients.ZapiIntegratorClient;
import com.guilinares.clinikai.presentation.controllers.dto.ClinicWhatsappManualConfigResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClinicWhatsappUsecaseConfig {

    @Bean
    public ClinicWhatsappProvisionUseCase clinicWhatsappProvisionUseCase(
            ClinicRepositoryPort clinicRepositoryPort,
            ClinicWhatsappRepositoryPort clinicWhatsappRepositoryPort,
            ZapiIntegratorClient zapiIntegratorClient
    ) {
        return new ClinicWhatsappProvisionUseCase(clinicRepositoryPort, clinicWhatsappRepositoryPort, zapiIntegratorClient);    }

    @Bean
    public ClinicWhatsappManualConfigUseCase clinicWhatsappManualConfigUseCase(
            ClinicRepositoryPort clinicRepositoryPort,
            ClinicWhatsappRepositoryPort clinicWhatsappRepositoryPort
    ) {
        return new ClinicWhatsappManualConfigUseCase(clinicRepositoryPort, clinicWhatsappRepositoryPort);
    }

    @Bean
    public ClinicWhatsappStatusUseCase clinicWhatsappStatusUseCase(
            ClinicWhatsappRepositoryPort clinicWhatsappRepositoryPort,
            ZapiIntegratorClient zapiIntegratorClient
    ) {
        return new ClinicWhatsappStatusUseCase(clinicWhatsappRepositoryPort, zapiIntegratorClient);
    }

    @Bean
    public ClinicWhatsappQrCodeUseCase clinicWhatsappQrCodeUseCase(
            ClinicWhatsappRepositoryPort clinicWhatsappRepositoryPort,
            ZapiIntegratorClient zapiIntegratorClient
    ) {
        return new ClinicWhatsappQrCodeUseCase(clinicWhatsappRepositoryPort, zapiIntegratorClient);
    }
}
