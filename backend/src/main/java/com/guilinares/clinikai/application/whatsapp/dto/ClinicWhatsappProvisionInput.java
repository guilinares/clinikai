package com.guilinares.clinikai.application.whatsapp.dto;

public record ClinicWhatsappProvisionInput(
        String name,
        String sessionName,
        Boolean isDevice,
        Boolean businessDevice
) {}
