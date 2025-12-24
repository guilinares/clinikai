package com.guilinares.clinicai.onboarding;

import com.guilinares.clinicai.clinic.ClinicEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(
        name = "onboarding_steps",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_onboarding_clinic_step_key",
                        columnNames = {"clinic_id", "step_key"}
                )
        }
)
public class OnboardingStepEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "clinic_id", nullable = false)
    private ClinicEntity clinic;

    /**
     * Identificador do passo dentro da clínica.
     * Ex.: "ASK_NAME", "ASK_AGE", "ASK_REASON", etc.
     */
    @Column(name = "step_key", nullable = false, length = 50)
    private String stepKey;

    /**
     * Campo lógico que esse passo preenche.
     * Ex.: "name", "age", "reason", "patientType", "urgent", "preferredDays"
     */
    @Column(name = "field_key", nullable = false, length = 50)
    private String fieldKey;

    /**
     * Pergunta que será enviada ao paciente nesse passo.
     * Pode conter placeholders como {name}, {age} etc.
     */
    @Column(name = "question", nullable = false, columnDefinition = "text")
    private String question;

    /**
     * Ordem do passo dentro do onboarding.
     */
    @Column(name = "order_index", nullable = false)
    private int orderIndex;

    /**
     * Indica se esse campo é obrigatório no onboarding.
     */
    @Column(name = "required", nullable = false)
    private boolean required;
}
