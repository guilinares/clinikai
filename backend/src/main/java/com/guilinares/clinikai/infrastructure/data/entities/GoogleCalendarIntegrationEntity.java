package com.guilinares.clinikai.infrastructure.data.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "google_calendar_integration")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoogleCalendarIntegrationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private UUID clinicId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String refreshTokenEncrypted;

    @Column(columnDefinition = "TEXT")
    private String accessTokenEncrypted;

    private Instant accessTokenExpiresAt;

    private String calendarId;

    private Instant connectedAt;

    private Instant revokedAt;
}
