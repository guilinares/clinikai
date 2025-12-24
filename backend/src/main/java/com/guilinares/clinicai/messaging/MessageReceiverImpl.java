package com.guilinares.clinicai.messaging;

import com.guilinares.clinicai.clinic.ClinicEntity;
import com.guilinares.clinicai.clinic.service.ClinicService;
import com.guilinares.clinicai.flow.dto.IncomingMessageRequest;
import com.guilinares.clinicai.flow.service.FlowService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageReceiverImpl implements MessageReceiver {

    private final ClinicService clinicService;
    private final FlowService flowService;

    @Override
    public void receiveMessage(String phone, String connectedPhone, String message) {
        try {
            ClinicEntity clinic = clinicService.findByPhone(connectedPhone);

            var result = flowService.handleIncomingMessage(clinic.getId(),
                    IncomingMessageRequest.builder()
                            .connectedPhone(clinic.getWhatsappNumber())
                            .phone(phone)
                            .type("WHATSAPP")
                            .text(IncomingMessageRequest.Text.builder()
                                    .message(message)
                                    .build())
                            .build()
            );
        } catch (Exception e) {
            System.out.printf("Falha no recebimento da mensagem: %s%n", e.getMessage());
        }
    }
}
