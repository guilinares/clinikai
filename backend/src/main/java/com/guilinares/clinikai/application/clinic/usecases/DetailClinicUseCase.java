package com.guilinares.clinikai.application.clinic.usecases;

import com.guilinares.clinikai.application.clinic.ports.ClinicRepositoryPort;
import com.guilinares.clinikai.domain.clinic.Clinic;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class DetailClinicUseCase {

    private final ClinicRepositoryPort clinicRepositoryPort;

    public Clinic execute(UUID clinicID) {
        Clinic clinic = Clinic.toDomain(clinicRepositoryPort.findById(clinicID).get());
        return clinic;
    }
}
