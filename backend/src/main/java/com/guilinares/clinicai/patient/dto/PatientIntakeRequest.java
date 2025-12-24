package com.guilinares.clinicai.patient.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PatientIntakeRequest {

    @NotBlank
    private String phone;          // sempre em formato E.164 ou algo consistente
    @NotBlank
    private String fullName;       // nome que a IA coletou
    private String email;          // se tiver
    private String patientType;    // "PARTICULAR", "CONVENIO", "NAO_INFORMADO" etc.
    /**
     * Campo livre para JSON em string com dados extras:
     * idade, motivo da consulta, canal, tags, etc.
     * Ex: {"motivo":"acne adulta","canal":"WHATSAPP","urgencia":false}
     */
    private String extraData;
}
