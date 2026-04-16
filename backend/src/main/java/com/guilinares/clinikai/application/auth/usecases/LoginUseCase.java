package com.guilinares.clinikai.application.auth.usecases;

import com.guilinares.clinikai.application.auth.dto.AuthTokens;
import com.guilinares.clinikai.application.auth.dto.LoginCommand;
import com.guilinares.clinikai.application.auth.ports.JwtTokenPort;
import com.guilinares.clinikai.application.auth.ports.PasswordHasherPort;
import com.guilinares.clinikai.application.auth.ports.UserRepositoryPort;
import com.guilinares.clinikai.application.clinic.ports.ClinicRepositoryPort;
import com.guilinares.clinikai.domain.clinic.ClinicStatus;
import com.guilinares.clinikai.domain.user.User;
import com.guilinares.clinikai.infrastructure.data.entities.ClinicEntity;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class LoginUseCase {

    private final UserRepositoryPort users;
    private final PasswordHasherPort hasher;
    private final JwtTokenPort jwt;
    private final ClinicRepositoryPort clinics;

    public LoginResponse execute(LoginCommand cmd) {
        var user = users.findByEmail(cmd.email().trim().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("Credenciais inválidas."));

        if (!hasher.matches(cmd.password(), user.passwordHash())) {
            throw new IllegalArgumentException("Credenciais inválidas.");
        }

        // Verifica status da clínica
        ClinicEntity clinic = clinics.findById(user.clinicId())
                .orElseThrow(() -> new IllegalArgumentException("Clínica não encontrada."));

        if (clinic.getStatus() == ClinicStatus.PENDING_APPROVAL) {
            throw new IllegalStateException("Sua clínica ainda está aguardando aprovação. Você receberá um email quando for aprovada.");
        }

        if (clinic.getStatus() == ClinicStatus.REJECTED) {
            throw new IllegalStateException("Sua clínica foi rejeitada. Entre em contato com o suporte.");
        }

        String token = jwt.createAccessToken(user.id(), user.clinicId(), user.email(), user.role().name());
        return new LoginResponse(token, user.clinicId(), new AuthUserResponse(
                user.id(),
                user.clinicId(),
                user.name(),
                user.email(),
                user.role().name()
        ));
    }

    public record LoginResponse(
            String accessToken,
            UUID clinicId,
            AuthUserResponse user
    ) {}

    public record AuthUserResponse(
            UUID id,
            UUID clinicId,
            String name,
            String email,
            String role
    ) {}
}
