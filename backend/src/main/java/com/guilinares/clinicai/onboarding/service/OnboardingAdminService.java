package com.guilinares.clinicai.onboarding.service;

import com.guilinares.clinicai.clinic.ClinicEntity;
import com.guilinares.clinicai.onboarding.OnboardingStepEntity;
import com.guilinares.clinicai.onboarding.OnboardingStepRepository;
import com.guilinares.clinicai.onboarding.dto.OnboardingStepRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class OnboardingAdminService {

    private final OnboardingStepRepository onboardingStepRepository;

    public OnboardingAdminService(OnboardingStepRepository onboardingStepRepository) {
        this.onboardingStepRepository = onboardingStepRepository;
    }

    public List<OnboardingStepEntity> listByClinic(UUID clinicId) {
        return onboardingStepRepository.findByClinicIdOrderByOrderIndexAsc(clinicId);
    }

    @Transactional
    public OnboardingStepEntity createStep(ClinicEntity clinic, OnboardingStepRequest request) {
        OnboardingStepEntity step = new OnboardingStepEntity();
        step.setClinic(clinic);
        step.setStepKey(request.stepKey());
        step.setFieldKey(request.fieldKey());
        step.setQuestion(request.question());
        step.setOrderIndex(request.orderIndex());
        step.setRequired(request.required());
        return onboardingStepRepository.save(step);
    }

    @Transactional
    public OnboardingStepEntity updateStep(ClinicEntity clinic, UUID stepId, OnboardingStepRequest request) {
        OnboardingStepEntity step = onboardingStepRepository.findById(stepId)
                .orElseThrow(() -> new IllegalArgumentException("Onboarding step não encontrado"));

        if (!step.getClinic().getId().equals(clinic.getId())) {
            throw new IllegalStateException("Step não pertence à clínica do usuário");
        }

        step.setStepKey(request.stepKey());
        step.setFieldKey(request.fieldKey());
        step.setQuestion(request.question());
        step.setOrderIndex(request.orderIndex());
        step.setRequired(request.required());

        return onboardingStepRepository.save(step);
    }

    @Transactional
    public void deleteStep(ClinicEntity clinic, UUID stepId) {
        OnboardingStepEntity step = onboardingStepRepository.findById(stepId)
                .orElseThrow(() -> new IllegalArgumentException("Onboarding step não encontrado"));

        if (!step.getClinic().getId().equals(clinic.getId())) {
            throw new IllegalStateException("Step não pertence à clínica do usuário");
        }

        onboardingStepRepository.delete(step);
    }
}
