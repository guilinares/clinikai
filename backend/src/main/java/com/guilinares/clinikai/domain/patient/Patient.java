package com.guilinares.clinikai.domain.patient;

import com.guilinares.clinikai.infrastructure.data.entities.PatientEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@Data
public class Patient {
    private UUID id;
    private UUID clinicId;
    private String fullName;
    private String lastMessage;
    private String phoneId;
    private String email;

    public static Patient toDomain(PatientEntity e) {
        return new Patient(
                e.getId(),
                e.getClinic().getId(),
                e.getFullName(),
                "",
                e.getPhone(),
                e.getEmail()
        );
    }
}
