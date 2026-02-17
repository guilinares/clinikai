package com.guilinares.clinikai.application.whatsapp.usecases;

import com.guilinares.clinikai.application.clinic.ports.ClinicRepositoryPort;
import com.guilinares.clinikai.application.whatsapp.dto.ClinicWhatsappProvisionInput;
import com.guilinares.clinikai.application.whatsapp.ports.ClinicWhatsappRepositoryPort;
import com.guilinares.clinikai.domain.enums.ClinicWhatsappProvider;
import com.guilinares.clinikai.domain.enums.ClinicWhatsappStatus;
import com.guilinares.clinikai.infrastructure.data.entities.ClinicWhatsappEntity;
import com.guilinares.clinikai.infrastructure.whatsapp.clients.ZapiIntegratorClient;
import com.guilinares.clinikai.presentation.controllers.dto.ClinicWhatsappProvisionRequest;
import com.guilinares.clinikai.presentation.controllers.dto.ClinicWhatsappProvisionResponse;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class ClinicWhatsappProvisionUseCase {

    private final ClinicRepositoryPort clinicRepositoryPort;
    private final ClinicWhatsappRepositoryPort clinicWhatsappRepositoryPort;
    private final ZapiIntegratorClient zapiIntegratorClient;

    public ClinicWhatsappProvisionResponse execute(UUID clinicId, ClinicWhatsappProvisionInput req) {
        var clinic = clinicRepositoryPort.findById(clinicId)
                .orElseThrow(() -> new IllegalArgumentException("Clinic not found: " + clinicId));

        var existingOpt = clinicWhatsappRepositoryPort.findByClinicId(clinicId);
        if (existingOpt.isPresent()) {
            var existing = existingOpt.get();

            // Idempotência: se já está provisionado/em andamento, só devolve.
            // Se quiser permitir retry automático quando status=ERROR, descomente o bloco abaixo.
            if (existing.getStatus() != ClinicWhatsappStatus.ERROR) {
                return toResponse(clinicId, existing);
            }

            // Retry quando está em ERROR: você decide a regra.
            // Eu vou permitir retry se vier uma chamada explícita (execute de novo).
            existing.setStatus(ClinicWhatsappStatus.PROVISIONING);
            existing.setLastErrorCode(null);
            existing.setLastErrorMessage(null);
            clinicWhatsappRepositoryPort.save(existing);

            return provisionRemoteAndUpdate(clinicId, existing, req);
        }

        // 1) cria registro local PROVISIONING antes de chamar a Z-API
        var placeholder = ClinicWhatsappEntity.builder()
                .clinic(clinic)
                .provider(ClinicWhatsappProvider.ZAPI)
                .status(ClinicWhatsappStatus.PROVISIONING)
                .baseUrl("https://api.z-api.io") // ou injete de config; se você já tem isso no client, melhor ainda
                .instanceId("PENDING")
                .instanceToken("PENDING")
                .shared(false)
                .lastErrorCode(null)
                .lastErrorMessage(null)
                .build();

        var saved = clinicWhatsappRepositoryPort.save(placeholder);

        // 2) chama Z-API e atualiza
        return provisionRemoteAndUpdate(clinicId, saved, req);
    }

    private ClinicWhatsappProvisionResponse provisionRemoteAndUpdate(
            UUID clinicId,
            ClinicWhatsappEntity entity,
            ClinicWhatsappProvisionInput req
    ) {
        try {
            var created = zapiIntegratorClient.createOnDemandInstance(
                    new ZapiIntegratorClient.CreateInstanceInput(
                            req.name(),
                            req.sessionName(),
                            req.isDevice(),
                            req.businessDevice()
                    )
            );

            entity.setBaseUrl(created.baseUrl());
            entity.setInstanceId(created.instanceId());
            entity.setInstanceToken(created.token());
            entity.setStatus(ClinicWhatsappStatus.AWAITING_QR);
            entity.setLastErrorCode(null);
            entity.setLastErrorMessage(null);

            var updated = clinicWhatsappRepositoryPort.save(entity);
            return toResponse(clinicId, updated);

        } catch (Exception e) {
            entity.setStatus(ClinicWhatsappStatus.ERROR);
            entity.setLastErrorCode("ZAPI_CREATE_INSTANCE");
            entity.setLastErrorMessage(safeMsg(e));
            var updated = clinicWhatsappRepositoryPort.save(entity);
            return toResponse(clinicId, updated);
        }
    }

    private ClinicWhatsappProvisionResponse toResponse(UUID clinicId, ClinicWhatsappEntity saved) {
        return new ClinicWhatsappProvisionResponse(
                clinicId,
                saved.getId(),
                saved.getProvider(),
                saved.getStatus(),
                saved.getBaseUrl(),
                saved.getInstanceId()
        );
    }

    private String safeMsg(Exception e) {
        var msg = e.getMessage();
        if (msg == null) return "Unknown error";
        // evita estourar coluna/texto gigantesco
        return msg.length() > 2000 ? msg.substring(0, 2000) : msg;
    }
}
