package com.guilinares.clinikai.presentation.controllers.dto;

import com.guilinares.clinikai.domain.enums.ClinicWhatsappStatus;

public record ClinicWhatsappQrResponse(
        String qrBase64,
        Integer expiresInSeconds
) {}