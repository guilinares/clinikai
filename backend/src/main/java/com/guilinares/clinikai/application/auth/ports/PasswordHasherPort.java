package com.guilinares.clinikai.application.auth.ports;

public interface PasswordHasherPort {
    String hash(String raw);
    boolean matches(String raw, String hash);
}
