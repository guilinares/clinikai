package com.guilinares.clinikai.infrastructure.n8n.adapters;

import com.guilinares.clinikai.application.clinic.ports.FlowRefinementPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class N8nFlowRefinementAdapter implements FlowRefinementPort {

    @Qualifier("n8nWebClient")
    private final WebClient n8nWebClient;

    @Override
    public void requestRefinement(UUID clinicId, String flowJson) {
        var payload = Map.of(
                "clinicId", clinicId.toString(),
                "flow", flowJson
        );

        n8nWebClient.post()
                .bodyValue(payload)
                .retrieve()
                .toBodilessEntity()
                .doOnError(e -> log.error("Erro ao enviar fluxo para refinamento no n8n. clinicId={}", clinicId, e))
                .subscribe();
    }
}
