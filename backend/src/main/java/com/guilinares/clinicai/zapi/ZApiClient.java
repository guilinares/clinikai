package com.guilinares.clinicai.zapi;

import com.guilinares.clinicai.messaging.MessageSender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@Slf4j
public class ZApiClient implements MessageSender {

    private final RestClient restClient;

    public ZApiClient(RestClient zapiRestClient) {
        this.restClient = zapiRestClient;
    }

    @Data
    @AllArgsConstructor
    public static class SendTextRequest {
        private String phone;   // ex.: 5511999999999
        private String message;
    }

    @Data
    public static class SendTextResponse {
        private String zaapId;
        private String messageId;
    }

    public void sendText(String phone, String message) {
        try {
            SendTextRequest body = new SendTextRequest(phone, message);

            SendTextResponse response = restClient.post()
                    .uri("/send-text")
                    .body(body)
                    .retrieve()
                    .body(SendTextResponse.class);

            log.info("Mensagem enviada via Z-API. phone={}, zaapId={}, messageId={}",
                    phone,
                    response != null ? response.getZaapId() : null,
                    response != null ? response.getMessageId() : null);
        } catch (Exception e) {
            log.error("Erro ao enviar mensagem via Z-API para {}: {}", phone, e.getMessage(), e);
            // aqui você pode jogar uma custom Exception se quiser tratar em outro nível
        }
    }
}
