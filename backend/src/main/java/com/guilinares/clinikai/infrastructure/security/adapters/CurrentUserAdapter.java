package com.guilinares.clinikai.infrastructure.security.adapters;

import com.guilinares.clinikai.application.auth.ports.CurrentUserPort;
import com.guilinares.clinikai.infrastructure.security.SecurityUserPrincipal;
import com.guilinares.clinikai.infrastructure.security.exceptions.UnauthorizedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CurrentUserAdapter implements  CurrentUserPort {

    @Override
    public UUID userId() {
        return principal().userId();
    }

    @Override
    public UUID clinicId() {
        return principal().clinicId();
    }

    @Override
    public String email() {
        return principal().email();
    }

    @Override
    public String role() {
        return principal().role();
    }

    private SecurityUserPrincipal principal() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new UnauthorizedException("Usuário não autenticado.");
        }
        if (!(auth.getPrincipal() instanceof SecurityUserPrincipal p)) {
            throw new UnauthorizedException("Principal inválido.");
        }
        return p;
    }
}
