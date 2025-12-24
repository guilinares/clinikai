package com.guilinares.clinicai.patient.service;

import com.guilinares.clinicai.clinic.ClinicEntity;
import com.guilinares.clinicai.clinic.ClinicRepository;
import com.guilinares.clinicai.patient.PatientEntity;
import com.guilinares.clinicai.patient.PatientRepository;
import com.guilinares.clinicai.patient.dto.PatientIntakeRequest;
import com.guilinares.clinicai.patient.dto.PatientResponse;
import com.guilinares.clinicai.security.UserDetailsImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final ClinicRepository clinicRepository;

    public PatientService(PatientRepository patientRepository,
                          ClinicRepository clinicRepository) {
        this.patientRepository = patientRepository;
        this.clinicRepository = clinicRepository;
    }

    @Transactional
    public PatientResponse intakeFromChannel(UserDetailsImpl currentUser,
                                             PatientIntakeRequest request) {

        var clinicId = currentUser.getClinicId();
        ClinicEntity clinic = clinicRepository.findById(clinicId)
                .orElseThrow(() -> new IllegalStateException("Clínica não encontrada para o usuário atual"));
        var normalizedPhone = normalizePhone(request.getPhone());
        var now = OffsetDateTime.now(ZoneOffset.UTC);
        var patientOpt = patientRepository.findByClinicIdAndPhone(clinic.getId(), normalizedPhone);
        PatientEntity patient;

        if (patientOpt.isPresent()) {
            // Atualiza os dados básicos do paciente
            patient = patientOpt.get();
            patient.setFullName(request.getFullName());
            if (request.getEmail() != null) {
                patient.setEmail(request.getEmail());
            }
            if (request.getPatientType() != null) {
                patient.setPatientType(request.getPatientType());
            }
            if (request.getExtraData() != null) {
                patient.setExtraData(request.getExtraData());
            }
            patient.setUpdatedAt(now);
        } else {
            // Cria novo paciente
            patient = new PatientEntity();
            patient.setClinic(clinic);
            patient.setFullName(request.getFullName());
            patient.setPhone(normalizedPhone);
            patient.setEmail(request.getEmail());
            patient.setPatientType(
                    request.getPatientType() != null ? request.getPatientType() : "NAO_INFORMADO"
            );
            patient.setExtraData(request.getExtraData());
            patient.setFirstContactAt(now);
            patient.setCreatedAt(now);
            patient.setUpdatedAt(now);
        }

        patient = patientRepository.save(patient);

        return toResponse(patient);
    }

    private String normalizePhone(String phone) {
        if (phone == null) return null;
        // simples: remove espaços e traços; depois você pode evoluir
        return phone.replaceAll("\\s+", "")
                .replaceAll("-", "");
    }

    private PatientResponse toResponse(PatientEntity patient) {
        return new PatientResponse(
                patient.getId(),
                patient.getFullName(),
                patient.getPhone(),
                patient.getEmail(),
                patient.getPatientType(),
                patient.getExtraData(),
                patient.getFirstContactAt()
        );
    }
}
