package com.guilinares.clinikai.presentation.webhooks;

import com.guilinares.clinikai.application.billing.usecases.HandleAsaasWebhookResponseUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/webhooks/asaas")
@RequiredArgsConstructor
public class AsaasWebhook {

    private final HandleAsaasWebhookResponseUseCase handleAsaasWebhookResponseUseCase;

    @Value("${asaas.webhook-token:}")
    private String webhookToken;

    @PostMapping
    public ResponseEntity<?> handle(
            @RequestHeader(value = "asaas-access-token", required = false) String token,
            @RequestBody HandleAsaasWebhookResponseUseCase.AsaasWebhookPayload payload) {

        if (!webhookToken.isBlank() && !webhookToken.equals(token)) {
            log.warn("Webhook Asaas rejeitado - token inválido. Recebido: {}", token);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        handleAsaasWebhookResponseUseCase.execute(payload);
        return ResponseEntity.ok().build();
    }
}
