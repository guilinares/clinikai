package com.guilinares.clinikai.application.patient.usecases;

import com.guilinares.clinikai.application.clinic.exceptions.ClinicaNaoEncontradaException;
import com.guilinares.clinikai.application.clinic.ports.ClinicRepositoryPort;
import com.guilinares.clinikai.application.patient.ports.PatientRepositoryPort;
import com.guilinares.clinikai.domain.patient.Patient;
import com.guilinares.clinikai.infrastructure.data.entities.ClinicEntity;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RegisterPatientUseCase {

    private final PatientRepositoryPort patients;
    private final ClinicRepositoryPort clinics;

    public Patient execute(String clinicPhone, String patientPhone) {
        ClinicEntity clinic = clinics.findByPhone(clinicPhone).orElseThrow(() -> new ClinicaNaoEncontradaException(clinicPhone));
        var patientOpt = patients.findByClinicIdAndPhone(clinic.getId(), patientPhone);
        return Patient.toDomain(patientOpt.orElseGet(() -> patients.save(clinic, patientPhone)));
    }
}
