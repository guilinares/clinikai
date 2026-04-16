package com.guilinares.clinikai.presentation.controllers;

import com.guilinares.clinikai.application.onboarding.dto.OnboardingRequest;
import com.guilinares.clinikai.application.onboarding.dto.OnboardingResponse;
import com.guilinares.clinikai.application.onboarding.usecases.OnboardingUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/onboarding")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OnboardingController {

    private final OnboardingUseCase onboardingUseCase;

    @PostMapping
    public ResponseEntity<OnboardingResponse> register(@Valid @RequestBody OnboardingRequest request) {
        OnboardingResponse response = onboardingUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
