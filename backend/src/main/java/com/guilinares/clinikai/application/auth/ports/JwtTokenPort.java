package com.guilinares.clinikai.application.auth.ports;

import java.util.UUID;

public interface JwtTokenPort {
    String createAccessToken(UUID userId, UUID clinicId, String email, String role);
}
