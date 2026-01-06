package com.guilinares.clinikai.application.auth.usecases;

import com.guilinares.clinikai.application.auth.dto.MeResult;
import com.guilinares.clinikai.application.auth.ports.UserRepositoryPort;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class GetMeUseCase {

    private final UserRepositoryPort users;

    public MeResult execute(UUID userId) {
        var user = users.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        return new MeResult(
                user.getId(),
                user.getClinicId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name()
        );
    }
}
