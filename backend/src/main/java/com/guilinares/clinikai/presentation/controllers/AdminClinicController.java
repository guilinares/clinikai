package com.guilinares.clinikai.presentation.controllers;

import com.guilinares.clinikai.application.onboarding.usecases.ApproveClinicUseCase;
import com.guilinares.clinikai.application.onboarding.usecases.RejectClinicUseCase;
import com.guilinares.clinikai.infrastructure.email.AdminActionTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/clinics")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminClinicController {

    private final ApproveClinicUseCase approveClinicUseCase;
    private final RejectClinicUseCase rejectClinicUseCase;
    private final AdminActionTokenService tokenService;

    @PostMapping("/{clinicId}/approve")
    public ResponseEntity<Map<String, String>> approve(@PathVariable UUID clinicId) {
        approveClinicUseCase.execute(clinicId);
        return ResponseEntity.ok(Map.of("message", "Clínica aprovada com sucesso."));
    }

    @PostMapping("/{clinicId}/reject")
    public ResponseEntity<Map<String, String>> reject(@PathVariable UUID clinicId) {
        rejectClinicUseCase.execute(clinicId);
        return ResponseEntity.ok(Map.of("message", "Clínica rejeitada."));
    }

    @GetMapping(value = "/{clinicId}/approve", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> approveViaEmail(@PathVariable UUID clinicId,
                                                   @RequestParam String token) {
        if (!tokenService.validateToken(clinicId, "approve", token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(buildHtmlResponse("Acesso negado", "Token inválido.", false));
        }
        try {
            approveClinicUseCase.execute(clinicId);
            return ResponseEntity.ok(buildHtmlResponse(
                    "Clínica Aprovada!",
                    "A clínica foi aprovada com sucesso. O usuário já pode fazer login.",
                    true
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(buildHtmlResponse("Erro", e.getMessage(), false));
        }
    }

    @GetMapping(value = "/{clinicId}/reject", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> rejectViaEmail(@PathVariable UUID clinicId,
                                                  @RequestParam String token) {
        if (!tokenService.validateToken(clinicId, "reject", token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(buildHtmlResponse("Acesso negado", "Token inválido.", false));
        }
        try {
            rejectClinicUseCase.execute(clinicId);
            return ResponseEntity.ok(buildHtmlResponse(
                    "Clínica Rejeitada",
                    "A clínica foi rejeitada.",
                    true
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(buildHtmlResponse("Erro", e.getMessage(), false));
        }
    }

    private String buildHtmlResponse(String title, String message, boolean success) {
        String color = success ? "#296374" : "#8c1e1e";
        String icon = success ? "&#10003;" : "&#10007;";
        return String.format("""
                <!DOCTYPE html>
                <html><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1">
                <title>%s - Clinikai</title>
                <style>
                  body{margin:0;min-height:100vh;display:flex;align-items:center;justify-content:center;
                       font-family:system-ui,-apple-system,sans-serif;background:#EDEDCE;}
                  .card{background:rgba(255,255,255,.7);border:1px solid rgba(12,44,85,.12);
                        border-radius:14px;padding:40px;max-width:440px;text-align:center;
                        box-shadow:0 10px 30px rgba(12,44,85,.12);}
                  .icon{width:64px;height:64px;border-radius:50%%;margin:0 auto 20px;
                        display:flex;align-items:center;justify-content:center;
                        font-size:28px;color:white;background:%s;}
                  h1{color:#0C2C55;font-size:24px;margin:0 0 12px;}
                  p{color:rgba(12,44,85,.7);font-size:15px;line-height:1.5;}
                </style></head>
                <body><div class="card">
                  <div class="icon">%s</div>
                  <h1>%s</h1>
                  <p>%s</p>
                </div></body></html>
                """, title, color, icon, title, message);
    }
}
