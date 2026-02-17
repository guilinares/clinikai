package com.guilinares.clinikai.application.whatsapp.usecases;

import com.guilinares.clinikai.application.whatsapp.ports.ClinicWhatsappRepositoryPort;
import com.guilinares.clinikai.domain.enums.ClinicWhatsappStatus;
import com.guilinares.clinikai.domain.exceptions.WhatsappSubscriptionRequiredException;
import com.guilinares.clinikai.infrastructure.whatsapp.clients.ZapiIntegratorClient;
import com.guilinares.clinikai.presentation.controllers.dto.ClinicWhatsappStatusResponse;
import lombok.RequiredArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@RequiredArgsConstructor
public class ClinicWhatsappStatusUseCase {

    private final ClinicWhatsappRepositoryPort repository;
    private final ZapiIntegratorClient zapiClient;

    public ClinicWhatsappStatusResponse execute(UUID clinicId) {

        var entity = repository.findByClinicId(clinicId)
                .orElseThrow(WhatsappSubscriptionRequiredException::new);

        try {
            var remote = zapiClient.checkConnection(
                    entity.getBaseUrl(),
                    entity.getInstanceId(),
                    entity.getInstanceToken()
            );

            ClinicWhatsappStatus st;
            String msg = null;

            if (remote.connected() && remote.smartphoneConnected()) {
                st = ClinicWhatsappStatus.CONNECTED;
            } else {
                st = ClinicWhatsappStatus.DISCONNECTED;
                msg = remote.error();
            }

            // Regra especial: assinatura necessária
            if (remote.error() != null && remote.error().toLowerCase().contains("subscribe")) {
                st = ClinicWhatsappStatus.SUSPENDED;
                msg = "Assinatura necessária para continuar enviando mensagens.";
                entity.setLastErrorCode("SUBSCRIPTION_REQUIRED");
                entity.setLastErrorMessage(remote.error());
            } else {
                // limpa erro se não tiver problema
                entity.setLastErrorCode(null);
                entity.setLastErrorMessage(null);
            }

            // persiste status local
            entity.setStatus(st);
            if (st == ClinicWhatsappStatus.CONNECTED) {
                entity.setLastConnectionAt(OffsetDateTime.now());
            }

            repository.save(entity);

            return new ClinicWhatsappStatusResponse(
                    st,
                    remote.connected(),
                    remote.session(),
                    remote.smartphoneConnected(),
                    remote.created(),
                    msg,
                    entity.getLastErrorCode(),
                    entity.getLastErrorMessage()
            );

        } catch (WhatsappSubscriptionRequiredException e) {
            entity.setStatus(ClinicWhatsappStatus.SUSPENDED);
            entity.setLastErrorCode("SUBSCRIPTION_REQUIRED");
            entity.setLastErrorMessage("Instance must be subscribed again");
            repository.save(entity);
            throw e;

        } catch (Exception e) {
            entity.setStatus(ClinicWhatsappStatus.ERROR);
            entity.setLastErrorCode("ZAPI_STATUS_ERROR");
            entity.setLastErrorMessage(safeMsg(e));
            repository.save(entity);

            // retorno bonito pro front mesmo em erro genérico
            return new ClinicWhatsappStatusResponse(
                    ClinicWhatsappStatus.ERROR,
                    false,
                    false,
                    false,
                    null,
                    "Não foi possível consultar o status agora. Tente novamente.",
                    entity.getLastErrorCode(),
                    entity.getLastErrorMessage()
            );
        }
    }

    private String safeMsg(Exception e) {
        var msg = e.getMessage();
        if (msg == null) return "Unknown error";
        return msg.length() > 2000 ? msg.substring(0, 2000) : msg;
    }
}
