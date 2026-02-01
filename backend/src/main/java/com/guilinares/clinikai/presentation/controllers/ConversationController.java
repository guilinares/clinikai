package com.guilinares.clinikai.presentation.controllers;

import com.guilinares.clinikai.application.conversation.usecases.MarkMessageProcessedUseCase;
import com.guilinares.clinikai.application.conversation.usecases.MessageClaimUseCase;
import com.guilinares.clinikai.application.conversation.usecases.RegisterConversationUseCase;
import com.guilinares.clinikai.presentation.controllers.dto.RegisterMessageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/conversation")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ConversationController {

    private final RegisterConversationUseCase registerConversationUseCase;
    private final MessageClaimUseCase messageClaimUseCase;
    private final MarkMessageProcessedUseCase markMessageProcessedUseCase;

    @PostMapping("/message")
    public ResponseEntity<RegisterConversationUseCase.ConversationResult> registerConversation(@RequestBody RegisterMessageRequest request) {
        var conversationResult = registerConversationUseCase.execute(request.clinicId(), request.patientId(), request.message(), request.direction());
        return ResponseEntity.ok().body(conversationResult);
    }

    @PostMapping("/{conversationId}/claim")
    public ResponseEntity<MessageClaimUseCase.ClaimResult> claimMessages(
            @PathVariable("conversationId") String conversationId,
            @RequestParam("max") int max) {
        var claimedResult = messageClaimUseCase.execute(conversationId, max);
        return ResponseEntity.accepted().body(claimedResult);
    }

    @PostMapping("/claims/{claimId}/processed")
    public ResponseEntity<Object> markClaimedAsProcessed(@PathVariable("claimId") String claimId) {
        markMessageProcessedUseCase.execute(claimId);
        return ResponseEntity.ok().build();
    }


}
