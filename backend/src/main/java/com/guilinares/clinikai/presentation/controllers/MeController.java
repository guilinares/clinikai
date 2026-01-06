package com.guilinares.clinikai.presentation.controllers;

import com.guilinares.clinikai.application.auth.dto.MeResult;
import com.guilinares.clinikai.application.auth.usecases.GetMeUseCase;
import com.guilinares.clinikai.infrastructure.security.SecurityUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MeController {

    private final GetMeUseCase getMeUseCase;

    @GetMapping
    public ResponseEntity<MeResult> me(@AuthenticationPrincipal SecurityUserPrincipal principal) {
        // principal vem do JWT filter
        MeResult me = getMeUseCase.execute(principal.getUserId());
        return ResponseEntity.ok(me);
    }
}
