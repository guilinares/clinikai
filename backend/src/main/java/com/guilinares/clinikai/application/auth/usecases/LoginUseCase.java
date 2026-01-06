package com.guilinares.clinikai.application.auth.usecases;

import com.guilinares.clinikai.application.auth.dto.AuthTokens;
import com.guilinares.clinikai.application.auth.dto.LoginCommand;
import com.guilinares.clinikai.application.auth.ports.JwtTokenPort;
import com.guilinares.clinikai.application.auth.ports.PasswordHasherPort;
import com.guilinares.clinikai.application.auth.ports.UserRepositoryPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LoginUseCase {

    private final UserRepositoryPort users;
    private final PasswordHasherPort hasher;
    private final JwtTokenPort jwt;

    public AuthTokens execute(LoginCommand cmd) {
        var user = users.findByEmail(cmd.email().trim().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("Credenciais inválidas."));

        if (!hasher.matches(cmd.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Credenciais inválidas.");
        }

        String token = jwt.createAccessToken(user.getId(), user.getClinicId(), user.getEmail(), user.getRole().name());
        return new AuthTokens(token);
    }
}
