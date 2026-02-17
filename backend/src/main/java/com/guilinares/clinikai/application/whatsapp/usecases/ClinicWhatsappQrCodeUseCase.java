package com.guilinares.clinikai.application.whatsapp.usecases;

import com.guilinares.clinikai.application.whatsapp.ports.ClinicWhatsappRepositoryPort;
import com.guilinares.clinikai.domain.enums.ClinicWhatsappStatus;
import com.guilinares.clinikai.infrastructure.whatsapp.clients.ZapiIntegratorClient;
import com.guilinares.clinikai.presentation.controllers.dto.ClinicWhatsappQrResponse;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class ClinicWhatsappQrCodeUseCase {

    private final ClinicWhatsappRepositoryPort repository;
    private final ZapiIntegratorClient zapiClient;

    public ClinicWhatsappQrResponse execute(UUID clinicId) {
        var entity = repository.findByClinicId(clinicId)
                .orElseThrow(() -> new IllegalArgumentException("WhatsApp not configured for clinic."));

        // Se já está conectado, você pode bloquear ou retornar 409
        if (entity.getStatus() == ClinicWhatsappStatus.CONNECTED) {
            throw new IllegalStateException("WhatsApp already connected.");
        }

        // Chama Z-API /qr-code/image (retorna base64) :contentReference[oaicite:4]{index=4}
        var base64 = zapiClient.getQrCodeImageBase64(
                entity.getBaseUrl(),
                entity.getInstanceId(),
                entity.getInstanceToken()
        );

        // A Z-API costuma retornar só o base64 "puro". Pro front ficar fácil, já prefixa data URI.
        String base64Pure = normalizeToPureBase64(base64); // ajuste pro seu modelo


        // Opcional: marca status como AWAITING_QR (ou mantém o atual)
        if (entity.getStatus() == ClinicWhatsappStatus.UNPROVISIONED
                || entity.getStatus() == ClinicWhatsappStatus.PROVISIONING
                || entity.getStatus() == ClinicWhatsappStatus.DISCONNECTED
                || entity.getStatus() == ClinicWhatsappStatus.ERROR) {
            entity.setStatus(ClinicWhatsappStatus.AWAITING_QR);
            repository.save(entity);
        }

        return new ClinicWhatsappQrResponse(
                base64Pure,
                20 // doc: QR invalida a cada ~20s :contentReference[oaicite:5]{index=5}
        );
    }

    private static String normalizeToPureBase64(String raw) {
        if (raw == null) return null;

        // Caso venha como "{value:...}"
        raw = raw.trim();
        if (raw.startsWith("{value:")) {
            raw = raw.substring("{value:".length()).trim();
            if (raw.endsWith("}")) raw = raw.substring(0, raw.length() - 1).trim();
        }

        // Caso venha "data:image/...;base64,AAAA"
        int idx = raw.indexOf("base64,");
        if (idx >= 0) {
            raw = raw.substring(idx + "base64,".length()).trim();
        }

        // remove aspas se tiver
        if ((raw.startsWith("\"") && raw.endsWith("\"")) || (raw.startsWith("'") && raw.endsWith("'"))) {
            raw = raw.substring(1, raw.length() - 1);
        }

        return raw;
    }
}