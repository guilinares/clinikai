package com.guilinares.clinikai.infrastructure.billing.dto;

public record AsaasPixQrCodeResponse(
        String encodedImage, // base64 png (normalmente vem assim)
        String payload       // "copia e cola"
) {}