package com.guilinares.clinikai.infrastructure;


import com.guilinares.clinikai.application.auth.ports.CurrentUserPort;
import com.guilinares.clinikai.application.auth.usecases.MeUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public MeUseCase meUseCase(CurrentUserPort currentUserPort) {
        return new MeUseCase(currentUserPort);
    }
}
