package com.guilinares.clinikai.infrastructure.n8n;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "n8n")
public record N8nProperties(
        String webhookUrl,
        String secret
) {
}
