package com.guilinares.clinikai.application.auth.usecases;


import com.guilinares.clinikai.application.auth.ports.CurrentUserPort;

import java.util.UUID;

public class MeUseCase {

    private final CurrentUserPort currentUser;

    public MeUseCase(CurrentUserPort currentUser) {
        this.currentUser = currentUser;
    }

    public Output execute() {
        return new Output(
                currentUser.userId(),
                currentUser.email(),
                currentUser.role(),
                currentUser.clinicId()
        );
    }

    public record Output(UUID userId, String email, String role, UUID clinicId) {}
}
