package com.guilinares.clinikai.presentation.controllers.dto;

import com.guilinares.clinikai.domain.enums.ClinicWhatsappProvider;
import com.guilinares.clinikai.domain.enums.ClinicWhatsappStatus;

import java.util.UUID;

public record ClinicWhatsappProvisionResponse(
        UUID clinicId,
        UUID clinicWhatsappId,
        ClinicWhatsappProvider provider,
        ClinicWhatsappStatus status,
        String baseUrl,
        String instanceId
) { }