package com.guilinares.clinikai.presentation.controllers.dto;

import jakarta.validation.constraints.NotBlank;

public record ClinicWhatsappProvisionRequest(
        @NotBlank String name,         // nome da instância no Z-API
        String sessionName,            // opcional
        Boolean isDevice,              // opcional
        Boolean businessDevice         // opcional
) { }