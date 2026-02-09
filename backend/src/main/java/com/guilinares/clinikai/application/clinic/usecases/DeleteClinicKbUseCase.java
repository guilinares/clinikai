package com.guilinares.clinikai.application.clinic.usecases;

import com.guilinares.clinikai.application.clinic.exceptions.FailDeleteKbException;
import com.guilinares.clinikai.application.clinic.ports.ClinicKbRepositoryPort;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class DeleteClinicKbUseCase {

    private final ClinicKbRepositoryPort clinicKb;

    public void execute(String kbId) {
        try {
            clinicKb.delete(UUID.fromString(kbId));
        } catch (Exception e) {
            throw new FailDeleteKbException("Falha ao deletar o Kb");
        }
    }
}
