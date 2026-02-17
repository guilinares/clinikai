package com.guilinares.clinikai.application.clinic.usecases;

import com.guilinares.clinikai.application.clinic.dto.ClinicRequest;
import com.guilinares.clinikai.application.clinic.exceptions.TelefoneJaPossuiClinicaException;
import com.guilinares.clinikai.application.clinic.ports.ClinicBillingRepositoryPort;
import com.guilinares.clinikai.application.clinic.ports.ClinicEventPublisherPort;
import com.guilinares.clinikai.application.clinic.ports.ClinicRepositoryPort;
import com.guilinares.clinikai.application.whatsapp.ports.ClinicWhatsappRepositoryPort;
import com.guilinares.clinikai.application.whatsapp.usecases.ClinicWhatsappProvisionUseCase;
import com.guilinares.clinikai.domain.clinic.Clinic;
import com.guilinares.clinikai.domain.clinic.ClinicRegisteredEvent;
import com.guilinares.clinikai.infrastructure.data.entities.ClinicEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
public class RegisterClinicUseCase {

    private final ClinicRepositoryPort clinics;
    private final ClinicBillingRepositoryPort clinicBilling;
    private final ClinicEventPublisherPort events;

    @Transactional
    public Clinic execute(ClinicRequest clinicRequest) {
        var clinicOpt = clinics.findByPhone(clinicRequest.whatsappNumber());
        if (clinics.findByPhone(clinicRequest.whatsappNumber()).isPresent())
            throw new TelefoneJaPossuiClinicaException(String.format(
                    "O telefone %s já possui uma clinica cadastrada.", clinicRequest.whatsappNumber()
            ));
        String billingDoc = trataDocumento(clinicRequest.documento());
        Clinic clinic = new Clinic(
                null,
                clinicRequest.name().trim(),
                clinicRequest.specialty().trim().toLowerCase(),
                clinicRequest.whatsappNumber().trim(),
                clinicRequest.email().trim(),
                billingDoc
        );
        ClinicEntity saved = clinics.save(clinic);
        clinicBilling.createClinicBilling(saved);

        events.publish(new ClinicRegisteredEvent(
                saved.getId(), saved.getName()
        ));

        return Clinic.toDomain(saved);
    }

    private String trataDocumento(String documento) {
        String doc = documento.replaceAll("\\D", "");
        if (!(doc.length() == 11 || doc.length() == 14)) {
            throw new IllegalArgumentException("CPF/CNPJ inválido");
        }
        return doc;
    }
}
