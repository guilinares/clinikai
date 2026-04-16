package com.guilinares.clinikai.application.onboarding.usecases;

import com.guilinares.clinikai.application.auth.ports.PasswordHasherPort;
import com.guilinares.clinikai.application.auth.ports.UserRepositoryPort;
import com.guilinares.clinikai.application.clinic.ports.ClinicRepositoryPort;
import com.guilinares.clinikai.application.onboarding.dto.OnboardingRequest;
import com.guilinares.clinikai.application.onboarding.dto.OnboardingResponse;
import com.guilinares.clinikai.application.onboarding.ports.OnboardingNotificationPort;
import com.guilinares.clinikai.domain.clinic.Clinic;
import com.guilinares.clinikai.domain.clinic.ClinicStatus;
import com.guilinares.clinikai.domain.user.User;
import com.guilinares.clinikai.domain.user.UserRole;
import com.guilinares.clinikai.infrastructure.data.entities.ClinicEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@RequiredArgsConstructor
public class OnboardingUseCase {

    private final ClinicRepositoryPort clinics;
    private final UserRepositoryPort users;
    private final PasswordHasherPort hasher;
    private final OnboardingNotificationPort notification;

    @Transactional
    public OnboardingResponse execute(OnboardingRequest req) {
        // Valida email único
        users.findByEmail(req.email().trim().toLowerCase()).ifPresent(u -> {
            throw new IllegalArgumentException("Email já cadastrado.");
        });

        // Valida telefone único
        clinics.findByPhone(req.whatsappNumber().trim()).ifPresent(c -> {
            throw new IllegalArgumentException(
                    String.format("O telefone %s já possui uma clínica cadastrada.", req.whatsappNumber())
            );
        });

        // Valida documento
        String billingDoc = trataDocumento(req.documento());

        // Cria clínica com status PENDING_APPROVAL
        Clinic clinic = new Clinic(
                null,
                req.clinicName().trim(),
                req.specialty().trim().toLowerCase(),
                req.whatsappNumber().trim(),
                req.email().trim().toLowerCase(),
                billingDoc,
                ClinicStatus.PENDING_APPROVAL
        );

        ClinicEntity savedClinic = clinics.save(clinic);

        // Cria usuário ADMIN da clínica
        var now = OffsetDateTime.now();
        var user = new User(
                null,
                savedClinic.getId(),
                req.userName().trim(),
                req.email().trim().toLowerCase(),
                hasher.hash(req.password()),
                UserRole.ADMIN,
                now,
                now
        );
        users.save(user);

        // Notifica admin por email (assíncrono)
        notification.notifyNewClinicRegistration(
                savedClinic.getId(),
                savedClinic.getName(),
                req.userName(),
                req.email(),
                req.whatsappNumber()
        );

        return new OnboardingResponse(
                savedClinic.getId(),
                savedClinic.getName(),
                ClinicStatus.PENDING_APPROVAL.name(),
                "Cadastro realizado com sucesso! Sua clínica está aguardando aprovação."
        );
    }

    private String trataDocumento(String documento) {
        String doc = documento.replaceAll("\\D", "");
        if (!(doc.length() == 11 || doc.length() == 14)) {
            throw new IllegalArgumentException("CPF/CNPJ inválido");
        }
        return doc;
    }
}
