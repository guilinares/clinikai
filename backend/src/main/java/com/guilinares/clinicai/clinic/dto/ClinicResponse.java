package com.guilinares.clinicai.clinic.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class ClinicResponse {

    private UUID id;
    private String name;
    private String specialty;
    private String whatsappNumber;
    private String timezone;
    private String aiConfig; // JSON em string
}
