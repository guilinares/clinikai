package com.guilinares.clinikai.infrastructure.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import java.util.UUID;

@Component
public class AdminActionTokenService {

    @Value("${app.admin.action-secret:${JWT_SECRET:change-me}}")
    private String secret;

    public String generateToken(UUID clinicId, String action) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            String data = clinicId.toString() + ":" + action;
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new RuntimeException("Falha ao gerar token", e);
        }
    }

    public boolean validateToken(UUID clinicId, String action, String token) {
        String expected = generateToken(clinicId, action);
        return expected.equals(token);
    }
}
