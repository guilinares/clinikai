package com.guilinares.clinicai.onboarding.dto;

import java.util.List;

public record OnboardingAiWizardResponse(
        List<OnboardingStepResponse> steps
) { }
