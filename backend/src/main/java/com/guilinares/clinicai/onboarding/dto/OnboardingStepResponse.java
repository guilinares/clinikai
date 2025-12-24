package com.guilinares.clinicai.onboarding.dto;

import java.util.UUID;

public record OnboardingStepResponse(
        UUID id,
        String stepKey,
        String fieldKey,
        String question,
        int orderIndex,
        boolean required
) { }
