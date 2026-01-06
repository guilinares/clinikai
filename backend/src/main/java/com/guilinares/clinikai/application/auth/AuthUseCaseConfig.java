package com.guilinares.clinikai.infrastructure.web.auth;

import com.guilinares.clinikai.application.auth.ports.*;
import com.guilinares.clinikai.application.auth.usecases.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthUseCaseConfig {

    @Bean
    public RegisterUserUseCase registerUserUseCase(UserRepositoryPort users, PasswordHasherPort hasher, JwtTokenPort jwt) {
        return new RegisterUserUseCase(users, hasher, jwt);
    }

    @Bean
    public LoginUseCase loginUseCase(UserRepositoryPort users, PasswordHasherPort hasher, JwtTokenPort jwt) {
        return new LoginUseCase(users, hasher, jwt);
    }

    @Bean
    public GetMeUseCase getMeUseCase(UserRepositoryPort users) {
        return new GetMeUseCase(users);
    }
}
