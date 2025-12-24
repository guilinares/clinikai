package com.guilinares.clinicai.zapi;

import com.guilinares.clinicai.clinic.ClinicEntity;
import com.guilinares.clinicai.clinic.service.ClinicService;
import com.guilinares.clinicai.flow.dto.IncomingMessageRequest;
import com.guilinares.clinicai.flow.service.FlowService;
import com.guilinares.clinicai.messaging.MessageReceiver;
import com.guilinares.clinicai.messaging.MessageSender;
import com.guilinares.clinicai.zapi.dto.ZApiIncomingMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/webhook/zapi")
@RequiredArgsConstructor
@Slf4j
public class ZApiWebhookController {

    private final FlowService flowService;
    private final ClinicService clinicService;
    private final MessageSender messageSender;
    private final MessageReceiver messageReceiver;

    @PostMapping("/messages")
    public ResponseEntity<Void> onMessage(@RequestBody ZApiIncomingMessage payload) {
        log.info("Webhook Z-API recebido: phone={}, fromMe={}, connectedPhone={}",
                payload.getPhone(), payload.getFromMe(), payload.getConnectedPhone());


        if (isNotPiloto(payload)) return ResponseEntity.ok().build();

        String message = payload.getMessageSafe();
        if (message == null || message.isBlank()) return ResponseEntity.ok().build();

        String phone = payload.getPhone();
        String connectedPhone = payload.getConnectedPhone();

        messageReceiver.receiveMessage(phone, connectedPhone, message);

        // Descobrir a clínica pelo telefone conectado (você pode ter esse campo na ClinicEntity)
        ClinicEntity clinic = clinicService.findByPhone(payload.getConnectedPhone());
        UUID clinicId = clinic.getId();

        // E.164: 55DDDNUMERO

        // Joga pro seu motor de fluxo
        var result = flowService.handleIncomingMessage(clinicId,
                IncomingMessageRequest.builder()
                        .connectedPhone(clinic.getWhatsappNumber())
                        .phone(phone)
                        .type(payload.getType())
                        .text(IncomingMessageRequest.Text.builder()
                                .message(message)
                                .build())
                        .build()
        );

         if (result != null) {
             messageSender.sendText(phone, result.getReplyMessage());
         }

        return ResponseEntity.ok().build();
    }

    private static boolean isNotPiloto(ZApiIncomingMessage payload) {
        return !List.of("5511987471054").contains(payload.getPhone());
    }
}
