package com.guilinares.clinicai.clinic;

import com.guilinares.clinicai.clinic.dto.ClinicResponse;
import com.guilinares.clinicai.clinic.dto.UpdateClinicRequest;
import com.guilinares.clinicai.clinic.service.ClinicService;
import com.guilinares.clinicai.security.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clinics")
@CrossOrigin(origins = "*")
public class ClinicController {

    private final ClinicService clinicService;

    public ClinicController(ClinicService clinicService) {
        this.clinicService = clinicService;
    }

    @GetMapping("/me")
    public ResponseEntity<ClinicResponse> getMyClinic(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        var response = clinicService.getMyClinic(userDetails);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/me")
    public ResponseEntity<ClinicResponse> updateMyClinic(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody UpdateClinicRequest request
    ) {
        var response = clinicService.updateMyClinic(userDetails, request);
        return ResponseEntity.ok(response);
    }
}
