package com.guilinares.clinikai.presentation.controllers;

import com.guilinares.clinikai.application.clinic.dto.ClinicRequest;
import com.guilinares.clinikai.application.clinic.usecases.DetailClinicUseCase;
import com.guilinares.clinikai.application.clinic.usecases.FindClinicByPhoneUsecase;
import com.guilinares.clinikai.application.clinic.usecases.RegisterClinicUseCase;
import com.guilinares.clinikai.domain.clinic.Clinic;
import com.guilinares.clinikai.infrastructure.security.SecurityUserPrincipal;
import com.guilinares.clinikai.presentation.controllers.dto.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clinics")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ClinicController {

    private final RegisterClinicUseCase registerClinicUseCase;
    private final DetailClinicUseCase detailClinicUseCase;
    private final FindClinicByPhoneUsecase findClinicByPhoneUsecase;

    @PostMapping("/register")
    public ResponseEntity<Clinic> registerClinic(@RequestBody ClinicRequest request) {
        Clinic clinic = registerClinicUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(clinic);
    }

    @GetMapping("/details")
    public ResponseEntity<Clinic> detailClinic(@AuthenticationPrincipal SecurityUserPrincipal userDetails) {
        Clinic clinic = detailClinicUseCase.execute(userDetails.clinicId());
        return ResponseEntity.status(HttpStatus.OK).body(clinic);
    }

    @GetMapping()
    public ResponseEntity<Clinic> findByPhone(@RequestParam String phone) {
        Clinic clinic = findClinicByPhoneUsecase.execute(phone);
        return ResponseEntity.status(HttpStatus.OK).body(clinic);
    }

}
