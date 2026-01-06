package com.guilinares.clinikai.infrastructure.security;

import com.guilinares.clinikai.application.auth.ports.JwtTokenPort;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokenService implements JwtTokenPort {

    private final JwtProperties props;
    private final SecretKey key;

    public JwtTokenService(JwtProperties props) {
        this.props = props;
        this.key = Keys.hmacShaKeyFor(props.secret().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String createAccessToken(UUID userId, UUID clinicId, String email, String role) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(props.accessTokenMinutes() * 60);

        return Jwts.builder()
                .issuer(props.issuer())
                .subject(userId.toString())
                .claim("clinicId", clinicId.toString())
                .claim("email", email)
                .claim("role", role)
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    public JwtClaims parseAndValidate(String token) {
        var jws = Jwts.parser()
                .verifyWith(key)
                .requireIssuer(props.issuer())
                .build()
                .parseSignedClaims(token);

        var c = jws.getPayload();
        return new JwtClaims(
                UUID.fromString(c.getSubject()),
                UUID.fromString(c.get("clinicId", String.class)),
                c.get("email", String.class),
                c.get("role", String.class)
        );
    }

    public record JwtClaims(UUID userId, UUID clinicId, String email, String role) {}
}
