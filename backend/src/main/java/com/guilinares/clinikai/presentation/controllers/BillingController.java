package com.guilinares.clinikai.presentation.controllers;

import com.guilinares.clinikai.application.billing.usecases.SubscribeBasicPixUseCase;
import com.guilinares.clinikai.infrastructure.billing.adapters.AsaasBillingAdapter;
import com.guilinares.clinikai.infrastructure.billing.dto.AsaasPixQrCodeResponse;
import com.guilinares.clinikai.infrastructure.security.SecurityUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/billing")
@RequiredArgsConstructor
public class BillingController {

    private final SubscribeBasicPixUseCase subscribeBasicPixUseCase;

    @PostMapping("/subscribe/basic")
    public SubscribeBasicPixUseCase.ClinicBillingDto subscribeBasic(@AuthenticationPrincipal SecurityUserPrincipal principal) {
        return subscribeBasicPixUseCase.execute(principal.clinicId());
    }

    @GetMapping
    public SubscribeBasicPixUseCase.ClinicBillingDto status(@AuthenticationPrincipal SecurityUserPrincipal principal) {
        return subscribeBasicPixUseCase.getStatus(principal.clinicId());
    }

    @GetMapping("/pix")
    public AsaasPixQrCodeResponse pix(@AuthenticationPrincipal SecurityUserPrincipal principal) {
        return subscribeBasicPixUseCase.getCurrentPixQr(principal.clinicId());
    }
}
