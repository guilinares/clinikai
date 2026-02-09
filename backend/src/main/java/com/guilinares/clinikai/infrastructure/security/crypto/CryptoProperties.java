package com.guilinares.clinikai.infrastructure.security.crypto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "security.crypto")
public class CryptoProperties {
    private String masterKeyBase64;
}
