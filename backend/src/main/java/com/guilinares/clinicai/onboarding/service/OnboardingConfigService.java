package com.guilinares.clinicai.onboarding.service;

import com.guilinares.clinicai.clinic.ClinicEntity;
import com.guilinares.clinicai.onboarding.OnboardingStepEntity;
import com.guilinares.clinicai.onboarding.OnboardingStepRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class OnboardingConfigService {

    private final OnboardingStepRepository onboardingStepRepository;

    public OnboardingConfigService(OnboardingStepRepository onboardingStepRepository) {
        this.onboardingStepRepository = onboardingStepRepository;
    }

    public List<OnboardingStepEntity> getStepsForClinic(UUID clinicId) {
        return onboardingStepRepository.findByClinicIdOrderByOrderIndexAsc(clinicId);
    }

    public Optional<OnboardingStepEntity> getFirstStep(ClinicEntity clinic) {
        return onboardingStepRepository
                .findByClinicIdOrderByOrderIndexAsc(clinic.getId())
                .stream()
                .findFirst();
    }

    public Optional<OnboardingStepEntity> getStep(ClinicEntity clinic, String stepKey) {
        return onboardingStepRepository.findByClinicIdAndStepKey(clinic.getId(), stepKey);
    }

    public Optional<OnboardingStepEntity> getNextStep(ClinicEntity clinic, String currentStepKey) {
        List<OnboardingStepEntity> steps =
                onboardingStepRepository.findByClinicIdOrderByOrderIndexAsc(clinic.getId());

        Map<String, OnboardingStepEntity> byKey =
                steps.stream().collect(Collectors.toMap(OnboardingStepEntity::getStepKey, s -> s));

        OnboardingStepEntity current = byKey.get(currentStepKey);
        if (current == null) return Optional.empty();

        return steps.stream()
                .filter(s -> s.getOrderIndex() > current.getOrderIndex())
                .sorted(Comparator.comparingInt(OnboardingStepEntity::getOrderIndex))
                .findFirst();
    }

    /**
     * Renderiza a pergunta substituindo placeholders como {name}, {age}, etc
     * a partir do state_data.
     */
    public String renderQuestion(OnboardingStepEntity step, Map<String, Object> state) {
        String text = step.getQuestion();
        if (state == null || state.isEmpty()) return text;

        String result = text;
        for (Map.Entry<String, Object> entry : state.entrySet()) {
            String placeholder = "{" + entry.getKey() + "}";
            result = result.replace(placeholder,
                    entry.getValue() != null ? entry.getValue().toString() : "");
        }
        return result;
    }
}
