package com.guilinares.clinikai.application.onboarding.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record OnboardingRequest(
        // Dados da clínica
        @NotBlank(message = "Nome da clínica é obrigatório")
        String clinicName,

        @NotBlank(message = "Especialidade é obrigatória")
        String specialty,

        @NotBlank(message = "WhatsApp é obrigatório")
        String whatsappNumber,

        @NotBlank(message = "Documento (CPF/CNPJ) é obrigatório")
        String documento,

        // Dados do usuário
        @NotBlank(message = "Nome do responsável é obrigatório")
        String userName,

        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email inválido")
        String email,

        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
        String password
) {}
