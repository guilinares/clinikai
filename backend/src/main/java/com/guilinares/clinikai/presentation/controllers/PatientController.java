package com.guilinares.clinikai.presentation.controllers;

import com.guilinares.clinikai.application.patient.usecases.RegisterPatientUseCase;
import com.guilinares.clinikai.domain.patient.Patient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PatientController {

    private final RegisterPatientUseCase registerPatientUseCase;

    @PutMapping("/new-message")
    public ResponseEntity<Patient> findOrRegisterPatient(@RequestParam String clinicPhone, @RequestParam String patientPhone) {
        Patient patient = registerPatientUseCase.execute(clinicPhone, patientPhone);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(patient);
    }
}
