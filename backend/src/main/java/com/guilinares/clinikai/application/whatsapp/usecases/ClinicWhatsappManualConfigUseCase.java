package com.guilinares.clinikai.application.whatsapp.usecases;

import com.guilinares.clinikai.application.clinic.ports.ClinicRepositoryPort;
import com.guilinares.clinikai.application.whatsapp.ports.ClinicWhatsappRepositoryPort;
import com.guilinares.clinikai.domain.enums.ClinicWhatsappStatus;
import com.guilinares.clinikai.infrastructure.data.entities.ClinicWhatsappEntity;
import com.guilinares.clinikai.presentation.controllers.dto.ClinicWhatsappManualConfigRequest;
import com.guilinares.clinikai.presentation.controllers.dto.ClinicWhatsappManualConfigResponse;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class ClinicWhatsappManualConfigUseCase {

    private final ClinicRepositoryPort clinicRepositoryPort;
    private final ClinicWhatsappRepositoryPort clinicWhatsappRepositoryPort;

    public ClinicWhatsappManualConfigResponse execute(UUID clinicId, ClinicWhatsappManualConfigRequest req) {
        var clinic = clinicRepositoryPort.findById(clinicId)
                .orElseThrow(() -> new IllegalArgumentException("Clinic not found: " + clinicId));

        // Validação extra (opcional, mas recomendado):
        // impedir você de colocar a MESMA instanceId em outra clínica sem querer.
        // Como seu port não tem esse método, eu sugiro adicionar:
        // Optional<ClinicWhatsappEntity> findByProviderAndInstanceId(provider, instanceId)
        // e aqui validar se pertence à mesma clínica.
        // (Se quiser, te passo como ajustar o adapter JPA.)

        var entity = clinicWhatsappRepositoryPort.findByClinicId(clinicId)
                .orElseGet(() -> {
                    var e = new ClinicWhatsappEntity();
                    e.setClinic(clinic);
                    return e;
                });

        entity.setProvider(req.provider());
        entity.setBaseUrl(req.baseUrl().trim());
        entity.setInstanceId(req.instanceId().trim());
        entity.setInstanceToken(req.instanceToken().trim());

        if (req.webhookUrl() != null) entity.setWebhookUrl(req.webhookUrl().trim());
        if (req.webhookSecret() != null) entity.setWebhookSecret(req.webhookSecret().trim());
        if (req.phoneE164() != null) entity.setPhoneE164(req.phoneE164().trim());

        if (req.shared() != null) entity.setShared(req.shared());

        // Status: se não veio no request, mantém o atual; se for novo registro, default AWAITING_QR
        if (req.status() != null) {
            entity.setStatus(req.status());
        } else if (entity.getStatus() == null || entity.getStatus() == ClinicWhatsappStatus.UNPROVISIONED) {
            entity.setStatus(ClinicWhatsappStatus.AWAITING_QR);
        }

        entity.setLastErrorCode(null);
        entity.setLastErrorMessage(null);

        var instanceId = req.instanceId().trim();

        var collisionOpt = clinicWhatsappRepositoryPort.findByProviderAndInstanceId(req.provider(), instanceId);

        if (collisionOpt.isPresent()) {
            var collision = collisionOpt.get();
            var collisionClinicId = collision.getClinic().getId();

            // Se a instância já pertence a outra clínica -> bloqueia
            if (!collisionClinicId.equals(clinicId)) {
                throw new IllegalStateException(
                        "Essa instanceId já está vinculada a outra clínica. clinicId=" + collisionClinicId
                );
            }
        }

        var saved = clinicWhatsappRepositoryPort.save(entity);

        return new ClinicWhatsappManualConfigResponse(
                clinicId,
                saved.getId(),
                saved.getProvider(),
                saved.getStatus(),
                saved.isShared(),
                saved.getBaseUrl(),
                saved.getInstanceId(),
                saved.getPhoneE164()
        );
    }
}
