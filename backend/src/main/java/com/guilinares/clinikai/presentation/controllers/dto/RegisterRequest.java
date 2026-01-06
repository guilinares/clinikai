package com.guilinares.clinikai.presentation.controllers.dto;

import jakarta.validation.constraints.*;

import java.util.UUID;

public record RegisterRequest(
        @NotNull UUID clinicId,
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8) String password
) {}
