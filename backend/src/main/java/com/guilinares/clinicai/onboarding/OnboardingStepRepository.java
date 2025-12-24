package com.guilinares.clinicai.onboarding;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OnboardingStepRepository extends JpaRepository<OnboardingStepEntity, UUID> {

    List<OnboardingStepEntity> findByClinicIdOrderByOrderIndexAsc(UUID clinicId);

    Optional<OnboardingStepEntity> findByClinicIdAndStepKey(UUID clinicId, String stepKey);

    void deleteByIdAndClinicId(UUID id, UUID clinicId);
}
