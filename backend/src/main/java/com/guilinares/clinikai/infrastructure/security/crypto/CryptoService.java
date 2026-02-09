package com.guilinares.clinikai.infrastructure.security.crypto;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Service
@EnableConfigurationProperties(CryptoProperties.class)
public class CryptoService {

    private static final String VERSION = "v1";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH_BITS = 128; // 16 bytes
    private static final int IV_LENGTH_BYTES = 12;      // recomendado p/ GCM

    private final SecretKey secretKey;
    private final SecureRandom secureRandom = new SecureRandom();

    public CryptoService(CryptoProperties props) {
        if (props.getMasterKeyBase64() == null || props.getMasterKeyBase64().isBlank()) {
            throw new IllegalStateException("CRYPTO_MASTER_KEY_BASE64 não configurada");
        }

        byte[] keyBytes = Base64.getDecoder().decode(props.getMasterKeyBase64());
        if (keyBytes.length != 32) {
            throw new IllegalStateException("CRYPTO_MASTER_KEY_BASE64 deve ter 32 bytes (256-bit) após Base64 decode");
        }

        this.secretKey = new SecretKeySpec(keyBytes, "AES");
    }

    public String encrypt(String plaintext) {
        if (plaintext == null) return null;

        try {
            byte[] iv = new byte[IV_LENGTH_BYTES];
            secureRandom.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));

            byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            return VERSION
                    + ":" + Base64.getEncoder().encodeToString(iv)
                    + ":" + Base64.getEncoder().encodeToString(ciphertext);

        } catch (Exception e) {
            throw new IllegalStateException("Falha ao criptografar", e);
        }
    }

    public String decrypt(String payload) {
        if (payload == null) return null;

        try {
            String[] parts = payload.split(":");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Payload inválido (esperado vX:iv:ciphertext)");
            }

            String version = parts[0];
            if (!VERSION.equals(version)) {
                throw new IllegalArgumentException("Versão de crypto não suportada: " + version);
            }

            byte[] iv = Base64.getDecoder().decode(parts[1]);
            byte[] ciphertext = Base64.getDecoder().decode(parts[2]);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));

            byte[] plaintextBytes = cipher.doFinal(ciphertext);
            return new String(plaintextBytes, StandardCharsets.UTF_8);

        } catch (Exception e) {
            throw new IllegalStateException("Falha ao descriptografar", e);
        }
    }
}
