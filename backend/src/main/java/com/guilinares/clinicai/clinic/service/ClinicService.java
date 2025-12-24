package com.guilinares.clinicai.clinic.service;

import com.guilinares.clinicai.clinic.ClinicEntity;
import com.guilinares.clinicai.clinic.ClinicRepository;
import com.guilinares.clinicai.clinic.dto.ClinicResponse;
import com.guilinares.clinicai.clinic.dto.UpdateClinicRequest;
import com.guilinares.clinicai.security.UserDetailsImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Service
public class ClinicService {

    private final ClinicRepository clinicRepository;

    public ClinicService(ClinicRepository clinicRepository) {
        this.clinicRepository = clinicRepository;
    }

    @Transactional(readOnly = true)
    public ClinicResponse getMyClinic(UserDetailsImpl currentUser) {
        var clinicId = currentUser.getClinicId();

        ClinicEntity clinic = clinicRepository.findById(clinicId)
                .orElseThrow(() -> new IllegalStateException("Clínica não encontrada para o usuário atual"));

        return toResponse(clinic);
    }

    @Transactional
    public ClinicResponse updateMyClinic(UserDetailsImpl currentUser, UpdateClinicRequest request) {
        var clinicId = currentUser.getClinicId();

        var clinic = clinicRepository.findById(clinicId)
                .orElseThrow(() -> new IllegalStateException("Clínica não encontrada para o usuário atual"));

        clinic.setName(request.getName());
        clinic.setSpecialty(request.getSpecialty());
        clinic.setWhatsappNumber(request.getWhatsappNumber());
        clinic.setTimezone(request.getTimezone());
        clinic.setAiConfig(request.getAiConfig());
        clinic.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));

        clinic = clinicRepository.save(clinic);

        return toResponse(clinic);
    }

    public ClinicEntity findByPhone(String connectedPhone) {
        return clinicRepository.findByWhatsappNumber(connectedPhone)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No clinic found with whatsappNumber = " + connectedPhone
                ));
    }

    private ClinicResponse toResponse(ClinicEntity clinic) {
        return new ClinicResponse(
                clinic.getId(),
                clinic.getName(),
                clinic.getSpecialty(),
                clinic.getWhatsappNumber(),
                clinic.getTimezone(),
                clinic.getAiConfig()
        );
    }
}
