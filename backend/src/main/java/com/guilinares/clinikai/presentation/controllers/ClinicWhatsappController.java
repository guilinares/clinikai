package com.guilinares.clinikai.presentation.controllers;

import com.guilinares.clinikai.application.whatsapp.dto.ClinicWhatsappProvisionInput;
import com.guilinares.clinikai.application.whatsapp.usecases.ClinicWhatsappManualConfigUseCase;
import com.guilinares.clinikai.application.whatsapp.usecases.ClinicWhatsappProvisionUseCase;
import com.guilinares.clinikai.application.whatsapp.usecases.ClinicWhatsappQrCodeUseCase;
import com.guilinares.clinikai.application.whatsapp.usecases.ClinicWhatsappStatusUseCase;
import com.guilinares.clinikai.infrastructure.security.SecurityUserPrincipal;
import com.guilinares.clinikai.presentation.controllers.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/whatsapp")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ClinicWhatsappController {

    private final ClinicWhatsappProvisionUseCase clinicWhatsappProvisionUseCase;
    private final ClinicWhatsappManualConfigUseCase manualConfigUseCase;
    private final ClinicWhatsappStatusUseCase clinicWhatsappStatusUseCase;
    private final ClinicWhatsappQrCodeUseCase clinicWhatsappQrCodeUseCase;

    @PostMapping("/provision")
    public ResponseEntity<ClinicWhatsappProvisionResponse> provision(
            @AuthenticationPrincipal SecurityUserPrincipal userDetails,
            @RequestBody @Valid ClinicWhatsappProvisionRequest request
    ) {
        var response = clinicWhatsappProvisionUseCase.execute(userDetails.clinicId(), new ClinicWhatsappProvisionInput(
                request.name(),
                request.sessionName(),
                request.isDevice(),
                request.businessDevice()
        ));
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/qr")
    public ClinicWhatsappQrResponse getQr(@AuthenticationPrincipal SecurityUserPrincipal userDetails) {
        return clinicWhatsappQrCodeUseCase.execute(userDetails.clinicId());
    }

    @GetMapping("/status")
    public ClinicWhatsappStatusResponse getStatus(
            @AuthenticationPrincipal SecurityUserPrincipal userDetails
    ) {
        return clinicWhatsappStatusUseCase.execute(userDetails.clinicId());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/clinics/{clinicId}/manual")
    public ResponseEntity<ClinicWhatsappManualConfigResponse> manualConfig(
            @PathVariable UUID clinicId,
            @RequestBody @Valid ClinicWhatsappManualConfigRequest req
    ) {
        var response = manualConfigUseCase.execute(clinicId, req);
        return ResponseEntity.ok().body(response);
    }
}
