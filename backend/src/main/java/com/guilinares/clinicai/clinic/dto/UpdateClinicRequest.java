package com.guilinares.clinicai.clinic.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UpdateClinicRequest {

    @NotBlank
    private String name;
    private String specialty;
    private String whatsappNumber;
    private String timezone;
    /**
     * JSON em string com configurações da IA.
     * Ex.: {"greeting":"Olá, sou a IA da clínica...","tone":"humano"}
     */
    private String aiConfig;

}
