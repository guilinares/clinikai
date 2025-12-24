package com.guilinares.clinicai.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class MeResponse {

    private UUID userId;
    private String name;
    private String email;
    private String role;
    private UUID clinicId;
    private String clinicName;

}
