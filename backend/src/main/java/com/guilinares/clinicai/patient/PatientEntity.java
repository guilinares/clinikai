package com.guilinares.clinicai.patient;

import com.guilinares.clinicai.clinic.ClinicEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "patients",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_patient_clinic_phone",
                columnNames = {"clinic_id", "phone"}
        )
)
@Getter @Setter
@Builder
@NoArgsConstructor @AllArgsConstructor
public class PatientEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "clinic_id")
    private ClinicEntity clinic;

    @Column(name = "full_name", nullable = false, length = 200)
    private String fullName;

    @Column(nullable = false, length = 30)
    private String phone;

    @Column(length = 200)
    private String email;

    @Column(name = "first_contact_at", nullable = false)
    private OffsetDateTime firstContactAt;

    @Column(name = "patient_type", length = 20)
    private String patientType; // NEW, RETURNING

    @Column(name = "extra_data", columnDefinition = "text")
    private String extraData;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
