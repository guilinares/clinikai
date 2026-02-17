package com.guilinares.clinikai.application.billing.usecases;

import com.guilinares.clinikai.application.billing.ports.BillingPort;
import com.guilinares.clinikai.application.clinic.exceptions.ClinicaNaoEncontradaException;
import com.guilinares.clinikai.application.clinic.ports.ClinicBillingRepositoryPort;
import com.guilinares.clinikai.application.clinic.ports.ClinicRepositoryPort;
import com.guilinares.clinikai.domain.enums.BillingPlan;
import com.guilinares.clinikai.domain.enums.BillingProvider;
import com.guilinares.clinikai.domain.enums.BillingStatus;
import com.guilinares.clinikai.infrastructure.billing.adapters.AsaasBillingAdapter;
import com.guilinares.clinikai.infrastructure.billing.dto.AsaasCustomerCreateRequest;
import com.guilinares.clinikai.infrastructure.billing.dto.AsaasPixQrCodeResponse;
import com.guilinares.clinikai.infrastructure.billing.dto.AsaasSubscriptionCreateRequest;
import com.guilinares.clinikai.infrastructure.data.entities.ClinicBillingEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@RequiredArgsConstructor
public class SubscribeBasicPixUseCase {

    private final BillingPort billingPort;
    private final ClinicRepositoryPort clinicRepositoryPort;
    private final ClinicBillingRepositoryPort clinicBillingRepositoryPort;

    private static final double BASIC_PRICE = 99.0;
    private static final String BASIC_DESC = "ClinikAI BASIC - Assinatura Mensal";

    @Transactional
    public ClinicBillingDto execute(UUID clinicId) {
        var billing = clinicBillingRepositoryPort.findByClinidId(clinicId).orElseGet(() ->
                createDefaultBilling(clinicId));

        if (billing.getStatus() == BillingStatus.ACTIVE && billing.getAsaasSubscriptionId() != null) {
            return ClinicBillingDto.from(clinicBillingRepositoryPort.save(billing));
        }

        var clinic = clinicRepositoryPort.findById(clinicId)
                .orElseThrow(() -> new ClinicaNaoEncontradaException("Clínica não encontrada"));

        if (billing.getAsaasCustomerId() == null) {
            var customer = billingPort.createCustomer(new AsaasCustomerCreateRequest(
                    clinic.getName(),
                    clinic.getEmail(),
                    clinic.getWhatsappNumber().substring(2),
                    clinic.getBillingDocument()
            ));
            billing.setAsaasCustomerId(customer.id());
            clinicBillingRepositoryPort.save(billing);
        }

        if (billing.getAsaasSubscriptionId() == null) {
            String nextDueDate = LocalDate.now().toString(); // cobra hoje
            var sub = billingPort.createSubscription(new AsaasSubscriptionCreateRequest(
                    billing.getAsaasCustomerId(),
                    "PIX",
                    "MONTHLY",
                    BASIC_PRICE,
                    nextDueDate,
                    BASIC_DESC
            ));
            billing.setAsaasSubscriptionId(sub.id());
        }

        billing.setProvider(BillingProvider.ASAAS);
        billing.setPlan(BillingPlan.BASIC);
        billing.setStatus(BillingStatus.PENDING);

        return ClinicBillingDto.from(clinicBillingRepositoryPort.save(billing));
    }

    @Transactional(readOnly = true)
    public ClinicBillingDto getStatus(UUID clinicId) {
        return ClinicBillingDto.from(clinicBillingRepositoryPort.findByClinidId(clinicId)
                .orElseGet(() -> createDefaultBilling(clinicId)));
    }

    @Transactional
    public AsaasPixQrCodeResponse getCurrentPixQr(UUID clinicId) {
        var billing = clinicBillingRepositoryPort.findByClinidId(clinicId)
                .orElseThrow(() -> new RuntimeException("Billing não encontrado"));

        if (billing.getLastPaymentId() == null) {
            throw new RuntimeException("Ainda não existe cobrança gerada para esta assinatura.");
        }

        var pix = billingPort.getPixOrQrCode(billing.getLastPaymentId());

        billing.setLastPixPayload(pix.payload());
        billing.setLastPixEncodedImage(pix.encodedImage());
        clinicBillingRepositoryPort.save(billing);

        return pix;
    }

    private ClinicBillingEntity createDefaultBilling(UUID clinicId) {
        var clinic = clinicRepositoryPort.findById(clinicId).orElseThrow();
        var billing = ClinicBillingEntity.builder()
                .clinicId(clinicId)
                .provider(BillingProvider.ASAAS)
                .plan(BillingPlan.BASIC)
                .status(BillingStatus.NO_SUBSCRIPTION)
                .build();
        return clinicBillingRepositoryPort.save(billing);
    }

    public record ClinicBillingDto(
            String provider,
            String plan,
            String status,
            String lastPaymentId
    ) {
        public static ClinicBillingDto from(ClinicBillingEntity e) {
            return new ClinicBillingDto(
                    e.getProvider().name(),
                    e.getPlan().name(),
                    e.getStatus().name(),
                    e.getLastPaymentId()
            );
        }
    }
}
