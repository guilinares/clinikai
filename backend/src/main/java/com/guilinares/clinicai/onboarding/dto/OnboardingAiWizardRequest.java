package com.guilinares.clinicai.onboarding.dto;

import jakarta.validation.constraints.NotBlank;

public record OnboardingAiWizardRequest(
        @NotBlank String description,
        boolean replaceExisting
) { }
