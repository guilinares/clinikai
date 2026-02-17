package com.guilinares.clinikai.infrastructure.billing.config;

import com.guilinares.clinikai.infrastructure.billing.BillingProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class AsaasWebClientConfig {

    private final BillingProperties props;

    @Bean
    public WebClient asaasWebClient() {
        return WebClient.builder()
                .baseUrl(props.baseUrl())
                .defaultHeader("access_token", props.apiKey())
                .build();
    }
}