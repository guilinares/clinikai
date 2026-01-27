package com.guilinares.clinikai.infrastructure.data.adapters;

import com.guilinares.clinikai.application.auth.ports.UserRepositoryPort;
import com.guilinares.clinikai.domain.user.User;
import com.guilinares.clinikai.domain.user.UserRole;
import com.guilinares.clinikai.infrastructure.data.entities.UserEntity;
import com.guilinares.clinikai.infrastructure.data.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserRepository repo;

    @Override
    public Optional<User> findByEmail(String email) {
        return repo.findByEmail(email).map(this::toDomain);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return repo.findById(id).map(this::toDomain);
    }

    @Override
    public User save(User user) {
        UserEntity entity = toEntity(user);
        UserEntity saved = repo.save(entity);
        return toDomain(saved);
    }

    private User toDomain(UserEntity e) {
        return new User(
                e.getId(),
                e.getClinicId(),
                e.getName(),
                e.getEmail(),
                e.getPasswordHash(),
                UserRole.valueOf(e.getRole()),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }

    private UserEntity toEntity(User u) {
        return UserEntity.builder()
                .id(u.id())
                .clinicId(u.clinicId())
                .name(u.name())
                .email(u.email())
                .passwordHash(u.passwordHash())
                .role(u.role().name())
                .createdAt(u.createdAt())
                .updatedAt(u.updatedAt())
                .build();
    }
}
