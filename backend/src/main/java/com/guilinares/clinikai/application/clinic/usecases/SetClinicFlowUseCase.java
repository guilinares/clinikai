package com.guilinares.clinikai.application.clinic.usecases;

import com.guilinares.clinikai.application.clinic.exceptions.ClinicaNaoEncontradaException;
import com.guilinares.clinikai.application.clinic.ports.ClinicRepositoryPort;
import com.guilinares.clinikai.application.clinic.ports.FlowRefinementPort;
import lombok.RequiredArgsConstructor;
import tools.jackson.databind.JsonNode;

import java.util.UUID;

@RequiredArgsConstructor
public class SetClinicFlowUseCase {

    private final ClinicRepositoryPort clinics;
    private final FlowRefinementPort flowRefinement;

    public void execute(UUID clinicId, JsonNode flow) {
        clinics.findById(clinicId)
                .orElseThrow(() -> new ClinicaNaoEncontradaException(
                        String.format("Clínica %s não encontrada.", clinicId)
                ));

        String flowJson = flow.toString();
        clinics.updateFlow(clinicId, flowJson);
        flowRefinement.requestRefinement(clinicId, flowJson);
    }
}
