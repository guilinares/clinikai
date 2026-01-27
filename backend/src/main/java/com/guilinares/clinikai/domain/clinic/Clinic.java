package com.guilinares.clinikai.domain.clinic;

import com.guilinares.clinikai.infrastructure.data.entities.ClinicEntity;

import java.util.UUID;

public record Clinic(UUID clinicId, String name, String specialty, String whatsappNumber) {

    public static Clinic toDomain(ClinicEntity entity) {
        return new Clinic(
                entity.getId(),
                entity.getName(),
                entity.getSpecialty(),
                entity.getWhatsappNumber()
        );
    }

    public static ClinicEntity toEntity(Clinic clinic) {
        return ClinicEntity.builder()
                .id(clinic.clinicId())
                .name(clinic.name())
                .specialty(clinic.specialty())
                .whatsappNumber(clinic.whatsappNumber())
                .build();
    }
}
