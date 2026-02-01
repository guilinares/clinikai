package com.guilinares.clinikai.application.clinic.usecases;

import com.guilinares.clinikai.application.clinic.dto.ClinicRequest;
import com.guilinares.clinikai.application.clinic.exceptions.TelefoneJaPossuiClinicaException;
import com.guilinares.clinikai.application.clinic.ports.ClinicRepositoryPort;
import com.guilinares.clinikai.domain.clinic.Clinic;
import lombok.RequiredArgsConstructor;

import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
public class RegisterClinicUseCase {

    private final ClinicRepositoryPort clinics;

    public Clinic execute(ClinicRequest clinicRequest) {
        var clinicOpt = clinics.findByPhone(clinicRequest.whatsappNumber());
        if (clinics.findByPhone(clinicRequest.whatsappNumber()).isPresent())
            throw new TelefoneJaPossuiClinicaException(String.format(
                    "O telefone %s já possui uma clinica cadastrada.", clinicRequest.whatsappNumber()
            ));
        Clinic clinic = new Clinic(
            null,
            clinicRequest.name().trim(),
            clinicRequest.specialty().trim().toLowerCase(),
            clinicRequest.whatsappNumber().trim()
        );
        return clinics.save(clinic);
    }
}
