package com.guilinares.clinikai.application.clinic.usecases;

import com.guilinares.clinikai.application.clinic.exceptions.ClinicaNaoEncontradaException;
import com.guilinares.clinikai.application.clinic.ports.ClinicRepositoryPort;
import com.guilinares.clinikai.domain.clinic.Clinic;
import com.guilinares.clinikai.infrastructure.data.entities.ClinicEntity;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class FindClinicByPhoneUsecase {

    private final ClinicRepositoryPort clinics;

    public Clinic execute(String phone) {
        Optional<ClinicEntity> clinic = clinics.findByPhone(phone);
        clinic.orElseThrow(() -> new ClinicaNaoEncontradaException(phone));
        return Clinic.toDomain(clinic.get());
    }
}
