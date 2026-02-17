package com.guilinares.clinikai.domain.clinic;

import com.guilinares.clinikai.infrastructure.data.entities.ClinicEntity;

import java.util.UUID;

public record Clinic(UUID clinicId, String name, String specialty, String whatsappNumber, String email, String billingDocument) {

    public static Clinic toDomain(ClinicEntity entity) {
        return new Clinic(
                entity.getId(),
                entity.getName(),
                entity.getSpecialty(),
                entity.getWhatsappNumber(),
                entity.getEmail(),
                entity.getBillingDocument()
        );
    }

    public static ClinicEntity toEntity(Clinic clinic) {
        return ClinicEntity.builder()
                .id(clinic.clinicId())
                .name(clinic.name())
                .specialty(clinic.specialty())
                .whatsappNumber(clinic.whatsappNumber())
                .email(clinic.email())
                .billingDocument(clinic.billingDocument())
                .build();
    }
}
