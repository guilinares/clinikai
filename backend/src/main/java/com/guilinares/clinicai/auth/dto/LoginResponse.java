package com.guilinares.clinicai.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class LoginResponse {

    private String token;
    private UUID userId;
    private String name;
    private String role;
    private UUID clinicId;
    private String clinicName;
}
