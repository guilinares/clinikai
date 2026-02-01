package com.guilinares.clinikai.presentation.controllers;

import com.guilinares.clinikai.application.clinic.dto.ClinicRequest;
import com.guilinares.clinikai.application.clinic.usecases.*;
import com.guilinares.clinikai.domain.clinic.Clinic;
import com.guilinares.clinikai.domain.clinic.ClinicKbCategory;
import com.guilinares.clinikai.infrastructure.pagination.PagedResponse;
import com.guilinares.clinikai.infrastructure.security.SecurityUserPrincipal;
import com.guilinares.clinikai.presentation.controllers.dto.AuthResponse;
import com.guilinares.clinikai.presentation.controllers.dto.RegisterClinicKbRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/clinics")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ClinicController {

    private final RegisterClinicUseCase registerClinicUseCase;
    private final DetailClinicUseCase detailClinicUseCase;
    private final FindClinicByPhoneUsecase findClinicByPhoneUsecase;
    private final ListClinicKbUseCase listClinicKbUseCase;
    private final RegisterClinicKbUseCase registerClinicKbUseCase;

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

    @GetMapping("/{clinicId}/kb")
    public ResponseEntity<PagedResponse<ListClinicKbUseCase.ClinicKbEntryResponse>> listKb(@PathVariable String clinicId,
                                                                                           @RequestParam(required = false) Boolean enabled,
                                                                                           @RequestParam(required = false) String category,
                                                                                           @RequestParam(required = false) String tag,
                                                                                           Pageable pageable) {
        var response = listClinicKbUseCase.execute(clinicId, enabled, category, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/{clinicId}/kb")
    public ResponseEntity<ListClinicKbUseCase.ClinicKbEntryResponse> registerKb(@PathVariable("clinicId") String clinicId,
                                                                                @RequestBody RegisterClinicKbRequest request) {
        var response = registerClinicKbUseCase.execute(clinicId, request.title(), request.content(), request.category());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
