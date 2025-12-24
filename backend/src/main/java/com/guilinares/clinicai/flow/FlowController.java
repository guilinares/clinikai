package com.guilinares.clinicai.flow;

import com.guilinares.clinicai.flow.dto.AssistantReplyResponse;
import com.guilinares.clinicai.flow.dto.IncomingMessageRequest;
import com.guilinares.clinicai.flow.service.FlowService;
import com.guilinares.clinicai.security.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/flows")
@CrossOrigin(origins = "*")
public class FlowController {

    private final FlowService flowService;

    public FlowController(FlowService flowService) {
        this.flowService = flowService;
    }

    /**
     * Endpoint de teste (mock) para simular mensagens vindas do WhatsApp.
     * Depois você pode plugar isso num webhook real.
     */
//    @PostMapping("/mock-message")
//    public ResponseEntity<AssistantReplyResponse> handleMockMessage(
//            @AuthenticationPrincipal UserDetailsImpl userDetails,
//            @Valid @RequestBody IncomingMessageRequest request
//    ) {
//        var response = flowService.handleIncomingMessage(userDetails, request);
//        return ResponseEntity.ok(response);
//    }
}
