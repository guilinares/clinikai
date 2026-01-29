package com.guilinares.clinikai.application.patient;

import com.guilinares.clinikai.application.clinic.ports.ClinicRepositoryPort;
import com.guilinares.clinikai.application.patient.ports.PatientRepositoryPort;
import com.guilinares.clinikai.application.patient.usecases.RegisterPatientUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PatientUseCaseConfig {

    @Bean
    public RegisterPatientUseCase registerPatientUseCase(PatientRepositoryPort patientPort, ClinicRepositoryPort clinicPort) {
        return new RegisterPatientUseCase(patientPort, clinicPort);
    }
}
