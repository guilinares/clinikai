package com.guilinares.clinikai.application.auth.dto;

import java.util.UUID;

public record MeResult(
        UUID userId,
        UUID clinicId,
        String name,
        String email,
        String role
) {}
