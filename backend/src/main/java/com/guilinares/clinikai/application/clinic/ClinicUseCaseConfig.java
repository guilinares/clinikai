package com.guilinares.clinikai.application.clinic;

import com.guilinares.clinikai.application.clinic.ports.ClinicKbRepositoryPort;
import com.guilinares.clinikai.application.clinic.ports.ClinicRepositoryPort;
import com.guilinares.clinikai.application.clinic.usecases.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClinicUseCaseConfig {

    @Bean
    public RegisterClinicUseCase registerClinicUseCase(ClinicRepositoryPort port) {
        return new RegisterClinicUseCase(port);
    }

    @Bean
    public DetailClinicUseCase detailClinicUseCase(ClinicRepositoryPort port) {
        return new DetailClinicUseCase(port);
    }

    @Bean
    public FindClinicByPhoneUsecase findClinicByPhoneUsecase(ClinicRepositoryPort port) {
        return new FindClinicByPhoneUsecase(port);
    }

    @Bean
    public ListClinicKbUseCase listClinicKbUseCase(ClinicRepositoryPort port, ClinicKbRepositoryPort clinicKbRepositoryPort) {
        return new ListClinicKbUseCase(clinicKbRepositoryPort, port);
    }

    @Bean
    public RegisterClinicKbUseCase registerClinicKbUseCase(ClinicRepositoryPort port, ClinicKbRepositoryPort clinicKbRepositoryPort) {
        return new RegisterClinicKbUseCase(clinicKbRepositoryPort, port);
    }
}
