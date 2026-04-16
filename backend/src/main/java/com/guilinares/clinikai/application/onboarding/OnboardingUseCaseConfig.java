package com.guilinares.clinikai.application.onboarding;

import com.guilinares.clinikai.application.auth.ports.PasswordHasherPort;
import com.guilinares.clinikai.application.auth.ports.UserRepositoryPort;
import com.guilinares.clinikai.application.clinic.ports.ClinicRepositoryPort;
import com.guilinares.clinikai.application.onboarding.ports.OnboardingNotificationPort;
import com.guilinares.clinikai.application.onboarding.usecases.ApproveClinicUseCase;
import com.guilinares.clinikai.application.onboarding.usecases.OnboardingUseCase;
import com.guilinares.clinikai.application.onboarding.usecases.RejectClinicUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OnboardingUseCaseConfig {

    @Bean
    public OnboardingUseCase onboardingUseCase(ClinicRepositoryPort clinics,
                                                UserRepositoryPort users,
                                                PasswordHasherPort hasher,
                                                OnboardingNotificationPort notification) {
        return new OnboardingUseCase(clinics, users, hasher, notification);
    }

    @Bean
    public ApproveClinicUseCase approveClinicUseCase(ClinicRepositoryPort clinics) {
        return new ApproveClinicUseCase(clinics);
    }

    @Bean
    public RejectClinicUseCase rejectClinicUseCase(ClinicRepositoryPort clinics) {
        return new RejectClinicUseCase(clinics);
    }
}
