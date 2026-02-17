package com.guilinares.clinikai.application.billing.usecases;

import com.guilinares.clinikai.application.billing.exceptions.HandleBillingException;
import com.guilinares.clinikai.application.clinic.ports.ClinicBillingRepositoryPort;
import com.guilinares.clinikai.domain.enums.BillingStatus;
import com.guilinares.clinikai.infrastructure.billing.exceptions.AsaasWebhookNotSuportedEvent;
import com.guilinares.clinikai.infrastructure.data.entities.ClinicBillingEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class HandleAsaasWebhookResponseUseCase {

    private final ClinicBillingRepositoryPort clinicBillingRepositoryPort;

    @Transactional
    public void execute(AsaasWebhookPayload payload) {
        try {
            var payment = payload.payment();
            if (payment == null || payment.subscription() == null) {
                throw new HandleBillingException("Falha ao processar retorno de billing.");
            }

            var billingOpt = clinicBillingRepositoryPort.findByAsaasSubscriptionId(payment.subscription());
            if (billingOpt.isEmpty()) {
                throw new HandleBillingException("Falha ao processar retorno de billing.");
            }
            var billing = handleBillingStatus(payload, billingOpt.get(), payment);
            if (billing == null) throw new AsaasWebhookNotSuportedEvent(payload);

            clinicBillingRepositoryPort.save(billing);
        } catch (AsaasWebhookNotSuportedEvent e) {
            throw e;
        } catch (Exception e) {
            throw new HandleBillingException("Falha ao processar retorno de billing.");
        }
    }

    private static ClinicBillingEntity handleBillingStatus(AsaasWebhookPayload payload, ClinicBillingEntity billing, AsaasWebhookPayment payment) {
        switch (payload.event()) {
            case "PAYMENT_CREATED" -> {
                billing.setLastPaymentId(payment.id());
                billing.setStatus(BillingStatus.PENDING);
            }
            case "PAYMENT_RECEIVED", "PAYMENT_CONFIRMED" -> {
                billing.setStatus(BillingStatus.ACTIVE);
            }
            case "PAYMENT_OVERDUE" -> {
                billing.setStatus(BillingStatus.PAST_DUE);
            }
            default -> {
                return null;
            }
        }
        return billing;
    }

    public record AsaasWebhookPayload(
            String event,
            AsaasWebhookPayment payment
    ) {}

    public record AsaasWebhookPayment(
            String id,
            String status,
            String subscription
    ) {}
}
