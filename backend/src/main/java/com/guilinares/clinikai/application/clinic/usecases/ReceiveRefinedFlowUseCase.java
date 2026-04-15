package com.guilinares.clinikai.application.clinic.usecases;

import com.guilinares.clinikai.application.clinic.exceptions.ClinicaNaoEncontradaException;
import com.guilinares.clinikai.application.clinic.ports.ClinicRepositoryPort;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class ReceiveRefinedFlowUseCase {

    private final ClinicRepositoryPort clinics;

    public void execute(UUID clinicId, String promptFinal) {
        clinics.findById(clinicId)
                .orElseThrow(() -> new ClinicaNaoEncontradaException(
                        String.format("Clínica %s não encontrada.", clinicId)
                ));

        clinics.updateFlowPrompt(clinicId, promptFinal);
    }
}
