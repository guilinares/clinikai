package com.guilinares.clinikai.presentation.webhooks;

import com.guilinares.clinikai.application.billing.usecases.HandleAsaasWebhookResponseUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/webhooks/asaas")
@RequiredArgsConstructor
public class AsaasWebhook {

    private final HandleAsaasWebhookResponseUseCase handleAsaasWebhookResponseUseCase;

    @PostMapping
    public ResponseEntity<?> handle(@RequestBody HandleAsaasWebhookResponseUseCase.AsaasWebhookPayload payload) {
        handleAsaasWebhookResponseUseCase.execute(payload);
        return ResponseEntity.ok().build();
    }

}
