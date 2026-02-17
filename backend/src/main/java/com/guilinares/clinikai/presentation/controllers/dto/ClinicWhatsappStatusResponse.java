package com.guilinares.clinikai.presentation.controllers.dto;

import com.guilinares.clinikai.domain.enums.ClinicWhatsappStatus;

import java.time.OffsetDateTime;

public record ClinicWhatsappStatusResponse(
        ClinicWhatsappStatus status,
        boolean connected,
        boolean session,
        boolean smartphoneConnected,
        Long created,
        String message,
        String lastErrorCode,
        String lastErrorMessage
) {}