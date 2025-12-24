package com.guilinares.clinicai.patient;

import com.guilinares.clinicai.patient.dto.PatientIntakeRequest;
import com.guilinares.clinicai.patient.dto.PatientResponse;
import com.guilinares.clinicai.patient.service.PatientService;
import com.guilinares.clinicai.security.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patients")
@CrossOrigin(origins = "*")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    /**
     * Endpoint genérico para "entrada" de paciente/lead de um canal (ex.: WhatsApp).
     * A IA pode chamar isso sempre que tiver os dados mínimos do paciente.
     */
    @PostMapping("/intake")
    public ResponseEntity<PatientResponse> intake(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody PatientIntakeRequest request
    ) {
        var response = patientService.intakeFromChannel(userDetails, request);
        return ResponseEntity.ok(response);
    }
}
