package com.guilinares.clinikai.application.auth.ports;

import com.guilinares.clinikai.domain.user.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepositoryPort {
    Optional<User> findByEmail(String email);
    Optional<User> findById(UUID id);
    User save(User user);
}
