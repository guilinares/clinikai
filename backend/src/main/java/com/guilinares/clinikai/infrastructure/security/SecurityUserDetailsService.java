package com.guilinares.clinikai.infrastructure.security;

import com.guilinares.clinikai.application.auth.ports.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityUserDetailsService implements UserDetailsService {

    private final UserRepositoryPort users;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var user = users.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new SecurityUserPrincipal(
                user.getId(),
                user.getClinicId(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getRole().name()
        );
    }
}
