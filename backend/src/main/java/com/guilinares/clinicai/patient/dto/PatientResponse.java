package com.guilinares.clinicai.patient.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class PatientResponse {

    private UUID id;
    private String fullName;
    private String phone;
    private String email;
    private String patientType;
    private String extraData;
    private OffsetDateTime firstContactAt;
}
