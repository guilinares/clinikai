package com.guilinares.clinikai.presentation.controllers;

import com.guilinares.clinikai.application.google.ports.GoogleCallback;
import com.guilinares.clinikai.application.google.ports.GoogleUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/integrations/google")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GoogleIntegrationController {

    private final GoogleUtils googleUtils;
    private final GoogleCallback googleCallback;

    @GetMapping("/authorize-url")
    public String authorize(@RequestParam String clinicId) {
        return googleUtils.buildAuthorizationUrl(UUID.fromString(clinicId));
    }

    @GetMapping("/callback")
    public void callback(@RequestParam String code,
                         @RequestParam String state) {
        googleCallback.handle(code, UUID.fromString(state));
    }
}
