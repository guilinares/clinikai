package com.guilinares.clinikai.application.clinic.usecases;

import com.guilinares.clinikai.application.clinic.exceptions.FailDeleteKbException;
import com.guilinares.clinikai.application.clinic.exceptions.FailSetEnabledKbException;
import com.guilinares.clinikai.application.clinic.ports.ClinicKbRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
public class SetEnabledClinicKbUseCase {

    private final ClinicKbRepositoryPort clinicKb;

    @Transactional
    public void execute(String kbId, Boolean enabled) {
        try {
            clinicKb.setEnabled(UUID.fromString(kbId), enabled);
        } catch (Exception e) {
            throw new FailSetEnabledKbException("Falha ao deletar o Kb");
        }
    }
}
