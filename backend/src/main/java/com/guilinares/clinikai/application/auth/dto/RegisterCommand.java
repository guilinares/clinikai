package com.guilinares.clinikai.application.auth.dto;

import java.util.UUID;

public record RegisterCommand(
        UUID clinicId,
        String name,
        String email,
        String password
) {}
