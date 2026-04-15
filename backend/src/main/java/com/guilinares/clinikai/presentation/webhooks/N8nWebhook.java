package com.guilinares.clinikai.presentation.webhooks;

import com.guilinares.clinikai.application.clinic.usecases.ReceiveRefinedFlowUseCase;
import com.guilinares.clinikai.infrastructure.n8n.N8nProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/webhooks/n8n")
@RequiredArgsConstructor
public class N8nWebhook {

    private final ReceiveRefinedFlowUseCase receiveRefinedFlowUseCase;
    private final N8nProperties n8nProperties;

    /**
     * Recebe o resultado do agente de IA do n8n após o refinamento do fluxo.
     *
     * Payload esperado (configurar no HTTP Request do n8n):
     * {
     *   "clinicId": "uuid",
     *   "promptFinal": "...",
     *   "melhorasSugeridas": ["..."]
     * }
     */
    @PostMapping("/flow-refined")
    public ResponseEntity<?> handleFlowRefined(
            @RequestBody Payload payload
    ) {
        receiveRefinedFlowUseCase.execute(payload.getClinicId(), payload.getPromptFinal());
        return ResponseEntity.ok().build();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Payload {
        private UUID clinicId;
        private String promptFinal;
        private List<String> melhorasSugeridas;
    }
}
