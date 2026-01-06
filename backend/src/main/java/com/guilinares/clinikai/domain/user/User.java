package com.guilinares.clinikai.domain.user;

import lombok.Value;

import java.time.OffsetDateTime;
import java.util.UUID;

@Value
public class User {
    UUID id;
    UUID clinicId;
    String name;
    String email;
    String passwordHash;
    UserRole role;
    OffsetDateTime createdAt;
    OffsetDateTime updatedAt;
}
