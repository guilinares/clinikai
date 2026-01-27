package com.guilinares.clinikai.application.clinic;

import com.guilinares.clinikai.application.clinic.ports.ClinicRepositoryPort;
import com.guilinares.clinikai.application.clinic.usecases.DetailClinicUseCase;
import com.guilinares.clinikai.application.clinic.usecases.FindClinicByPhoneUsecase;
import com.guilinares.clinikai.application.clinic.usecases.RegisterClinicUseCase;
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
}
