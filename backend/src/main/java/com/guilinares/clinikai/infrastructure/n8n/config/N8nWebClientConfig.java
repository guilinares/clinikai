package com.guilinares.clinikai.infrastructure.n8n.config;

import com.guilinares.clinikai.infrastructure.n8n.N8nProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(N8nProperties.class)
public class N8nWebClientConfig {

    private final N8nProperties props;

    @Bean
    public WebClient n8nWebClient() {
        return WebClient.builder()
                .baseUrl(props.webhookUrl())
                .defaultHeader("X-N8n-Secret", props.secret())
                .build();
    }
}
