package com.guilinares.clinikai.presentation.controllers.dto;

import com.guilinares.clinikai.domain.enums.ClinicWhatsappProvider;
import com.guilinares.clinikai.domain.enums.ClinicWhatsappStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ClinicWhatsappManualConfigRequest(

        @NotNull ClinicWhatsappProvider provider,

        @NotBlank String baseUrl,
        @NotBlank String instanceId,
        @NotBlank String instanceToken,

        // opcionais
        ClinicWhatsappStatus status,
        Boolean shared,
        String webhookUrl,
        String webhookSecret,
        String phoneE164
) {}