package com.guilinares.clinicai.onboarding.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OnboardingStepRequest(
        @NotBlank String stepKey,
        @NotBlank String fieldKey,
        @NotBlank String question,
        @NotNull Integer orderIndex,
        @NotNull Boolean required
) { }
