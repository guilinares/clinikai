package com.guilinares.clinicai.zapi.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ZApiIncomingMessage {

    private String phone;          // número do paciente (E.164)
    private String connectedPhone; // telefone conectado na instância
    private Boolean fromMe;        // se a msg veio do próprio número da instância
    private String type;           // "ReceivedCallback", etc.

    private Text text;

    @Getter
    @Setter
    public static class Text {
        private String message;    // texto da mensagem
    }

    public boolean isFromMeSafe() {
        return Boolean.TRUE.equals(fromMe);
    }

    public String getMessageSafe() {
        return text != null ? text.getMessage() : null;
    }
}
