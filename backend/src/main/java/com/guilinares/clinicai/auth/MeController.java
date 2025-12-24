package com.guilinares.clinicai.auth;

import com.guilinares.clinicai.auth.dto.MeResponse;
import com.guilinares.clinicai.security.UserDetailsImpl;
import com.guilinares.clinicai.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/me")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class MeController {

    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<MeResponse> me(@AuthenticationPrincipal UserDetailsImpl userDetails) {

        MeResponse response = new MeResponse(
                userDetails.getId(),
                userDetails.getName(),
                userDetails.getUsername(),   // email
                userDetails.getRoleName(),
                userDetails.getClinicId(),
                userDetails.getClinicName()
        );

        return ResponseEntity.ok(response);
    }
}
