package com.guilinares.clinikai.presentation.controllers;

import com.guilinares.clinikai.application.auth.dto.*;
import com.guilinares.clinikai.application.auth.usecases.*;
import com.guilinares.clinikai.domain.user.User;
import com.guilinares.clinikai.presentation.controllers.dto.AuthResponse;
import com.guilinares.clinikai.presentation.controllers.dto.LoginRequest;
import com.guilinares.clinikai.presentation.controllers.dto.RegisterRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUseCase loginUseCase;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
        AuthTokens tokens = registerUserUseCase.execute(
                new RegisterCommand(req.clinicId(), req.name(), req.email(), req.password())
        );
        return ResponseEntity.ok(new AuthResponse(tokens.accessToken()));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginUseCase.LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        LoginUseCase.LoginResponse response = loginUseCase.execute(new LoginCommand(req.email(), req.password()));
        return ResponseEntity.ok(response);
    }
}
