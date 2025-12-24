package com.guilinares.clinicai.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    Optional<UserEntity> findByEmail(String email);

    @Query("""
            select u
            from UserEntity u
            join fetch u.clinic c
            where u.email = :email
            """)
    Optional<UserEntity> findByEmailWithClinic(@Param("email") String email);
}
