package com.guilinares.clinikai.infrastructure.google;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "google.oauth")
public record GoogleProperties(
        String clientId,
        String clientSecret,
        String redirectUri
) {}
