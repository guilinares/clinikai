package com.guilinares.clinikai.application.auth.usecases;

import com.guilinares.clinikai.application.auth.dto.AuthTokens;
import com.guilinares.clinikai.application.auth.dto.RegisterCommand;
import com.guilinares.clinikai.application.auth.ports.JwtTokenPort;
import com.guilinares.clinikai.application.auth.ports.PasswordHasherPort;
import com.guilinares.clinikai.application.auth.ports.UserRepositoryPort;
import com.guilinares.clinikai.domain.user.User;
import com.guilinares.clinikai.domain.user.UserRole;
import lombok.RequiredArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@RequiredArgsConstructor
public class RegisterUserUseCase {

    private final UserRepositoryPort users;
    private final PasswordHasherPort hasher;
    private final JwtTokenPort jwt;

    public AuthTokens execute(RegisterCommand cmd) {
        users.findByEmail(cmd.email()).ifPresent(u -> {
            throw new IllegalArgumentException("Email já cadastrado.");
        });

        var now = OffsetDateTime.now();
        var user = new User(
                null,
                cmd.clinicId(),
                cmd.name().trim(),
                cmd.email().trim().toLowerCase(),
                hasher.hash(cmd.password()),
                UserRole.ADMIN, // primeiro usuário da clínica pode ser ADMIN
                now,
                now
        );

        User saved = users.save(user);

        String token = jwt.createAccessToken(saved.id(), saved.clinicId(), saved.email(), saved.role().name());
        return new AuthTokens(token);
    }
}
