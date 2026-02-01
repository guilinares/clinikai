package com.guilinares.clinikai.presentation.controllers.dto;

import jakarta.validation.constraints.*;
import java.util.List;

public record RegisterClinicKbRequest(

        @NotBlank
        @Size(max = 200)
        String title,

        @NotBlank
        @Size(max = 20000) // limite razoável
        String content,

        @NotBlank
        String category,   // vem como string, você converte via ClinicKbCategory.from()

        @Size(max = 20)
        List<@NotBlank @Size(max = 50) String> tags,

        Boolean enabled    // opcional, default true
) {}
