package com.guilinares.clinicai.onboarding;

import com.guilinares.clinicai.clinic.ClinicEntity;
import com.guilinares.clinicai.clinic.ClinicRepository;
import com.guilinares.clinicai.onboarding.dto.OnboardingAiWizardRequest;
import com.guilinares.clinicai.onboarding.dto.OnboardingAiWizardResponse;
import com.guilinares.clinicai.onboarding.dto.OnboardingStepRequest;
import com.guilinares.clinicai.onboarding.dto.OnboardingStepResponse;
import com.guilinares.clinicai.onboarding.service.OnboardingAdminService;
import com.guilinares.clinicai.onboarding.service.OnboardingWizardService;
import com.guilinares.clinicai.security.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/onboarding")
@CrossOrigin(origins = "*")
public class OnboardingAdminController {

    private final OnboardingAdminService onboardingAdminService;
    private final OnboardingWizardService onboardingWizardService;
    private final ClinicRepository clinicRepository;

    public OnboardingAdminController(OnboardingAdminService onboardingAdminService,
                                     OnboardingWizardService onboardingWizardService,
                                     ClinicRepository clinicRepository) {
        this.onboardingWizardService = onboardingWizardService;
        this.onboardingAdminService = onboardingAdminService;
        this.clinicRepository = clinicRepository;
    }

    private ClinicEntity getClinic(UserDetailsImpl userDetails) {
        return clinicRepository.findById(userDetails.getClinicId())
                .orElseThrow(() -> new IllegalStateException("Clínica não encontrada para o usuário atual"));
    }

    @PostMapping("/wizard/ai-generate")
    public ResponseEntity<OnboardingAiWizardResponse> aiGenerateOnboarding(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody OnboardingAiWizardRequest request
    ) {
        ClinicEntity clinic = getClinic(userDetails);

        var steps = onboardingWizardService.generateOnboardingWithAi(
                clinic,
                request.description(),
                request.replaceExisting()
        );

        var responses = steps.stream()
                .map(s -> new OnboardingStepResponse(
                        s.getId(),
                        s.getStepKey(),
                        s.getFieldKey(),
                        s.getQuestion(),
                        s.getOrderIndex(),
                        s.isRequired()
                ))
                .toList();

        return ResponseEntity.ok(new OnboardingAiWizardResponse(responses));
    }


    @GetMapping("/steps")
    public ResponseEntity<List<OnboardingStepResponse>> listSteps(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        ClinicEntity clinic = getClinic(userDetails);
        var steps = onboardingAdminService.listByClinic(clinic.getId())
                .stream()
                .map(s -> new OnboardingStepResponse(
                        s.getId(),
                        s.getStepKey(),
                        s.getFieldKey(),
                        s.getQuestion(),
                        s.getOrderIndex(),
                        s.isRequired()
                ))
                .toList();

        return ResponseEntity.ok(steps);
    }

    @PostMapping("/steps")
    public ResponseEntity<OnboardingStepResponse> createStep(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody OnboardingStepRequest request
    ) {
        ClinicEntity clinic = getClinic(userDetails);
        var step = onboardingAdminService.createStep(clinic, request);

        return ResponseEntity.ok(new OnboardingStepResponse(
                step.getId(),
                step.getStepKey(),
                step.getFieldKey(),
                step.getQuestion(),
                step.getOrderIndex(),
                step.isRequired()
        ));
    }

    @PutMapping("/steps/{id}")
    public ResponseEntity<OnboardingStepResponse> updateStep(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("id") UUID id,
            @Valid @RequestBody OnboardingStepRequest request
    ) {
        ClinicEntity clinic = getClinic(userDetails);
        var step = onboardingAdminService.updateStep(clinic, id, request);

        return ResponseEntity.ok(new OnboardingStepResponse(
                step.getId(),
                step.getStepKey(),
                step.getFieldKey(),
                step.getQuestion(),
                step.getOrderIndex(),
                step.isRequired()
        ));
    }

    @DeleteMapping("/steps/{id}")
    public ResponseEntity<Void> deleteStep(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("id") UUID id
    ) {
        ClinicEntity clinic = getClinic(userDetails);
        onboardingAdminService.deleteStep(clinic, id);
        return ResponseEntity.noContent().build();
    }
}
