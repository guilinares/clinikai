package com.guilinares.clinikai.infrastructure.whatsapp.clients;

import com.guilinares.clinikai.domain.exceptions.WhatsappSubscriptionRequiredException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
@EnableConfigurationProperties(ZapiProperties.class)
public class ZapiIntegratorRestClient implements ZapiIntegratorClient {

    private final RestClient restClient;
    private final ZapiProperties props;

    public ZapiIntegratorRestClient(RestClient.Builder builder, ZapiProperties props) {
        this.props = props;
        this.restClient = builder
                .baseUrl(props.baseUrl()) // ex: https://api.z-api.io
                .build();
    }

    @Override
    public CreateInstanceOutput createOnDemandInstance(CreateInstanceInput input) {
        // payload que a Z-API espera
        var body = new CreateInstanceRequest(
                input.name(),
                input.sessionName(),
                input.isDevice(),
                input.businessDevice()
        );

        var response = restClient.post()
                .uri("/instances/integrator/on-demand")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Client-Token", props.clientToken())
                .body(body)
                .retrieve()
                .body(CreateInstanceResponse.class);

        if (response == null || response.id == null || response.token == null) {
            throw new IllegalStateException("Z-API returned invalid response when creating instance.");
        }

        return new CreateInstanceOutput(response.id, response.token, props.baseUrl());
    }

    @Override
    public ZapiConnectionStatusDto checkConnection(String baseUrl, String instanceId, String token) {

        try {
            var response = restClient.get()
                    .uri("/instances/{instanceId}/token/{token}/status", instanceId, token)
                    .header("Client-Token", props.clientToken())
                    .retrieve()
                    .body(ZapiConnectionStatusDto.class);

            return response;
        } catch (RestClientResponseException e) {
            throw mapZapiError(e);
        }
    }

    @Override
    public String getQrCodeImageBase64(String baseUrl, String instanceId, String token) {
        try {
            var response = RestClient.create(baseUrl).get()
                    .uri("/instances/{instanceId}/token/{token}/qr-code/image", instanceId, token)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Client-Token", props.clientToken())
                    .retrieve()
                    .body(String.class);

            if (response == null || response.isBlank()) {
                throw new IllegalStateException("Z-API returned empty QR code image.");
            }

            return response.replace("\"", "").trim();
        } catch (RestClientResponseException e) {
            // Aqui você pode mapear:
            // - 400: já conectado / sessão ativa
            // - 401/403: token inválido
            throw e;
        }
    }

    // --- DTOs internos do client ---

    private record CreateInstanceRequest(
            String name,
            String sessionName,
            Boolean isDevice,
            Boolean businessDevice
    ) {}

    // Ajuste os nomes exatos conforme o JSON real retornado pela Z-API.
    // A doc normalmente retorna "id" e "token" (ou similar).
    private static final class CreateInstanceResponse {
        public String id;
        public String token;
    }


    private RuntimeException mapZapiError(RestClientResponseException e) {
        var body = e.getResponseBodyAsString();

        if (e.getStatusCode().value() == 400 &&
                body != null &&
                body.toLowerCase().contains("must subscribe to this instance again")) {
            return new WhatsappSubscriptionRequiredException();
        }

        return e;
    }
}
