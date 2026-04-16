package com.guilinares.clinikai.application.onboarding.dto;

import java.util.UUID;

public record OnboardingResponse(
        UUID clinicId,
        String clinicName,
        String status,
        String message
) {}
