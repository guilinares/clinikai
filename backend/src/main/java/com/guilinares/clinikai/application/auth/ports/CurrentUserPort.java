package com.guilinares.clinikai.application.auth.ports;

import java.util.UUID;

public interface CurrentUserPort {
    UUID userId();
    UUID clinicId();
    String name();
    String email();
    String role();
}
