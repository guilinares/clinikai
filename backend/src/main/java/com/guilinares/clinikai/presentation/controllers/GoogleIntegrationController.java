package com.guilinares.clinikai.presentation.controllers;

import com.guilinares.clinikai.application.google.ports.GoogleCallback;
import com.guilinares.clinikai.application.google.ports.GoogleUtils;
import com.guilinares.clinikai.application.google.usecases.GoogleConnectionStatusUseCase;
import com.guilinares.clinikai.infrastructure.google.adapters.GoogleUtilsAdapter;
import com.guilinares.clinikai.infrastructure.security.SecurityUserPrincipal;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/integrations/google")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GoogleIntegrationController {

    private final GoogleUtils googleUtils;
    private final GoogleCallback googleCallback;
    private final GoogleConnectionStatusUseCase googleStatus;

    @GetMapping("/status")
    public ResponseEntity<GoogleConnectionStatusUseCase.GoogleConnectionStatusResponse> status(@AuthenticationPrincipal SecurityUserPrincipal principal) {
        return ResponseEntity.ok().body(googleStatus.getStatus(principal.clinicId()));
    }

    @GetMapping("/authorize-url")
    public ResponseEntity<GoogleUtilsAdapter.GoogleUrlResponse> authorize(@AuthenticationPrincipal SecurityUserPrincipal principal) {
        return ResponseEntity.ok().body(googleUtils.buildAuthorizationUrl(principal.clinicId()));
    }

    @GetMapping("/callback")
    public void callback(@RequestParam String code,
                         @RequestParam String state,
                         HttpServletResponse response) throws IOException {
        String redirectUrl = googleCallback.handle(code, UUID.fromString(state));
        response.sendRedirect(redirectUrl);
    }
}
