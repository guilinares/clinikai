package com.guilinares.clinikai.application.clinic.usecases;

import com.guilinares.clinikai.application.clinic.exceptions.ClinicaNaoEncontradaException;
import com.guilinares.clinikai.application.clinic.ports.ClinicRepositoryPort;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class GetClinicFlowPromptUseCase {

    private final ClinicRepositoryPort clinics;

    public String execute(UUID clinicId) {
        return clinics.findById(clinicId)
                .orElseThrow(() -> new ClinicaNaoEncontradaException(
                        String.format("Clínica %s não encontrada.", clinicId)
                ))
                .getFlowPrompt();
    }
}
