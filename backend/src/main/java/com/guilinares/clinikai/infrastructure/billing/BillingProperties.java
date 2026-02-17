package com.guilinares.clinikai.infrastructure.billing;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "asaas")
public record BillingProperties(
        String baseUrl,
        String apiKey
) {
}
