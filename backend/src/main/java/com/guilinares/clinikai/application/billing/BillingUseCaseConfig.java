package com.guilinares.clinikai.application.billing;

import com.guilinares.clinikai.application.billing.ports.BillingEventRepositoryPort;
import com.guilinares.clinikai.application.billing.ports.BillingPort;
import com.guilinares.clinikai.application.billing.usecases.HandleAsaasWebhookResponseUseCase;
import com.guilinares.clinikai.application.billing.usecases.SubscribeBasicPixUseCase;
import com.guilinares.clinikai.application.clinic.ports.ClinicBillingRepositoryPort;
import com.guilinares.clinikai.application.clinic.ports.ClinicRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BillingUseCaseConfig {

    @Bean
    public HandleAsaasWebhookResponseUseCase handleAsaasWebhookResponseUseCase(
            ClinicBillingRepositoryPort clinicBillingRepositoryPort,
            BillingEventRepositoryPort billingEventRepositoryPort
    ) {
        return new HandleAsaasWebhookResponseUseCase(clinicBillingRepositoryPort, billingEventRepositoryPort);
    }

    @Bean
    public SubscribeBasicPixUseCase subscribeBasicPixUseCase(
           BillingPort billingPort,
        ClinicRepositoryPort clinicRepositoryPort,
        ClinicBillingRepositoryPort clinicBillingRepositoryPort
    ) {
        return new SubscribeBasicPixUseCase(billingPort, clinicRepositoryPort, clinicBillingRepositoryPort);
    }
}
