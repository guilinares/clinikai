package com.guilinares.clinikai.infrastructure.whatsapp.clients;

public interface ZapiIntegratorClient {

    record CreateInstanceInput(
            String name,
            String sessionName,
            Boolean isDevice,
            Boolean businessDevice
    ) {}

    record CreateInstanceOutput(
            String instanceId,
            String token,
            String baseUrl
    ) {}

    record ZapiConnectionStatusDto(
            boolean connected,
            boolean session,
            long created,
            String error,
            boolean smartphoneConnected
    ) {}

    CreateInstanceOutput createOnDemandInstance(CreateInstanceInput input);
    ZapiConnectionStatusDto checkConnection(String baseUrl, String instanceId, String token);
    String getQrCodeImageBase64(String baseUrl, String instanceId, String token);

}
