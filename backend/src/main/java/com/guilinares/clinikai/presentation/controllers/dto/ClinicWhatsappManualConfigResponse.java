package com.guilinares.clinikai.presentation.controllers.dto;

import com.guilinares.clinikai.domain.enums.ClinicWhatsappProvider;
import com.guilinares.clinikai.domain.enums.ClinicWhatsappStatus;

import java.util.UUID;

public record ClinicWhatsappManualConfigResponse(
        UUID clinicId,
        UUID clinicWhatsappId,
        ClinicWhatsappProvider provider,
        ClinicWhatsappStatus status,
        boolean shared,
        String baseUrl,
        String instanceId,
        String phoneE164
) {}