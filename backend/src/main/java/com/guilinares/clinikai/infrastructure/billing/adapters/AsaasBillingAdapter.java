package com.guilinares.clinikai.infrastructure.billing.adapters;

import com.guilinares.clinikai.application.billing.ports.BillingPort;
import com.guilinares.clinikai.infrastructure.billing.BillingProperties;
import com.guilinares.clinikai.infrastructure.billing.dto.*;
import com.guilinares.clinikai.infrastructure.google.GoogleProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(BillingProperties.class)
public class AsaasBillingAdapter implements BillingPort {

    private final BillingProperties props;
    private final WebClient asaasWebClient;

    @Override
    public AsaasCustomerResponse createCustomer(AsaasCustomerCreateRequest req) {
        return asaasWebClient.post()
                .uri("/customers")
                .bodyValue(req)
                .retrieve()
                .bodyToMono(AsaasCustomerResponse.class)
                .block();
    }

    @Override
    public AsaasSubscriptionResponse createSubscription(AsaasSubscriptionCreateRequest req) {
        try {
                return asaasWebClient.post()
                    .uri("/subscriptions")
                    .bodyValue(req)
                    .retrieve()
                    .bodyToMono(AsaasSubscriptionResponse.class)
                    .block();
        } catch (WebClientResponseException e) {
            var body = e.getResponseBodyAsString();
            throw new RuntimeException("Asaas createSubscription failed: " + e.getStatusCode() + " body=" + body, e);
        }
    }

    @Override
    public AsaasPixQrCodeResponse getPixOrQrCode(String paymentId) {
        try {
            return asaasWebClient.get()
                .uri("/payments/{id}/pixQrCode", paymentId)
                .retrieve()
                .bodyToMono(AsaasPixQrCodeResponse.class)
                .block();
        } catch (WebClientResponseException e) {
            var body = e.getResponseBodyAsString();
            throw new RuntimeException("Asaas getPixOrQrCode failed: " + e.getStatusCode() + " body=" + body, e);
        }
    }
}
