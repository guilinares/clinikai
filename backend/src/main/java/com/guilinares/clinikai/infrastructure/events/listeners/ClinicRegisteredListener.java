package com.guilinares.clinikai.infrastructure.events.listeners;


import com.guilinares.clinikai.application.whatsapp.dto.ClinicWhatsappProvisionInput;
import com.guilinares.clinikai.application.whatsapp.usecases.ClinicWhatsappProvisionUseCase;
import com.guilinares.clinikai.domain.clinic.ClinicRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClinicRegisteredListener {

    private final ClinicWhatsappProvisionUseCase provisionUseCase;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onClinicRegistered(ClinicRegisteredEvent event) {
        try {
//            var input = new ClinicWhatsappProvisionInput(
//                    "clinic-" + event.clinicId(),
//                    event.clinicName(),
//                    false,
//                    true
//            );
//            provisionUseCase.execute(event.clinicId(), input);
//            log.info("WhatsApp provisioned for clinicId={}", event.clinicId());
            log.info("ClinicRegisteredListener - Future implementation");
        } catch (Exception e) {
            log.error("Failed to provision WhatsApp for clinicId={}: {}", event.clinicId(), e.getMessage(), e);
        }
    }
}
