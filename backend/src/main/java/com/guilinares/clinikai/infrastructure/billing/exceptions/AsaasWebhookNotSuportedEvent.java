package com.guilinares.clinikai.infrastructure.billing.exceptions;

import com.guilinares.clinikai.application.billing.usecases.HandleAsaasWebhookResponseUseCase;

public class AsaasWebhookNotSuportedEvent extends RuntimeException {
    public HandleAsaasWebhookResponseUseCase.AsaasWebhookPayload payload;

    public AsaasWebhookNotSuportedEvent(HandleAsaasWebhookResponseUseCase.AsaasWebhookPayload payload) {
        this.payload = payload;
    }
}
