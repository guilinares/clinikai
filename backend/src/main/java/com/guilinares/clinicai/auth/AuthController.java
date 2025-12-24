package com.guilinares.clinicai.auth;


import com.guilinares.clinicai.auth.dto.LoginRequest;
import com.guilinares.clinicai.auth.dto.LoginResponse;
import com.guilinares.clinicai.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // depois você pode restringir pro domínio do front em produção
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
