package com.guilinares.clinikai.infrastructure.whatsapp.clients;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "zapi")
public record ZapiProperties(
        String baseUrl,
        String clientToken
) {}
