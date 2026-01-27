package com.guilinares.clinikai.presentation.controllers;

import com.guilinares.clinikai.application.auth.usecases.MeUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/me")
@CrossOrigin(origins = "*")
public class MeController {

    private final MeUseCase meUseCase;

    public MeController(MeUseCase meUseCase) {
        this.meUseCase = meUseCase;
    }

    @GetMapping
    public ResponseEntity<?> me() {
        return ResponseEntity.ok(meUseCase.execute());
    }
}