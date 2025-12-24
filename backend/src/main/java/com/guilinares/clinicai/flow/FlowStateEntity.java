package com.guilinares.clinicai.flow;

import com.guilinares.clinicai.clinic.ClinicEntity;
import com.guilinares.clinicai.patient.PatientEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(
        name = "flows_state",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_flows_state_clinic_phone",
                        columnNames = {"clinic_id", "phone"}
                )
        }
)
public class FlowStateEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "clinic_id", nullable = false)
    private ClinicEntity clinic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private PatientEntity patient;

    @Column(name = "phone", nullable = false, length = 30)
    private String phone;

    @Column(name = "channel", nullable = false, length = 30)
    private String channel; // "WHATSAPP", "INSTAGRAM", etc.

    @Column(name = "current_step", length = 50)
    private String currentStep; // "INITIAL", "ASK_REASON", "ASK_PATIENT_TYPE", etc.

    @Column(name = "state_data", columnDefinition = "jsonb")
    private String stateData; // JSON em string com infos do fluxo

    @Column(name = "last_user_message", columnDefinition = "text")
    private String lastUserMessage;

    @Column(name = "last_bot_message", columnDefinition = "text")
    private String lastBotMessage;

    @Column(name = "last_message_at")
    private OffsetDateTime lastMessageAt;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        var now = OffsetDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (!this.active) {
            this.active = true; // default
        }
        if (this.currentStep == null) {
            this.currentStep = "INITIAL";
        }
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }
}
