package com.guilinares.clinikai.application.billing.usecases;

import com.guilinares.clinikai.application.billing.ports.BillingEventRepositoryPort;
import com.guilinares.clinikai.application.billing.ports.BillingEventRepositoryPort.BillingEventEntry;
import com.guilinares.clinikai.application.clinic.ports.ClinicBillingRepositoryPort;
import com.guilinares.clinikai.domain.enums.BillingStatus;
import com.guilinares.clinikai.infrastructure.data.entities.ClinicBillingEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class HandleAsaasWebhookResponseUseCase {

    private final ClinicBillingRepositoryPort clinicBillingRepositoryPort;
    private final BillingEventRepositoryPort billingEventRepositoryPort;

    @Transactional
    public void execute(AsaasWebhookPayload payload) {
        String subscriptionId = extractSubscriptionId(payload);
        String paymentId = payload.payment() != null ? payload.payment().id() : null;
        String rawPayload = serialize(payload);

        if (subscriptionId == null) {
            log.info("Webhook Asaas sem subscription ID, evento={}", payload.event());
            return;
        }

        var billingOpt = clinicBillingRepositoryPort.findByAsaasSubscriptionId(subscriptionId);
        if (billingOpt.isEmpty()) {
            log.warn("Webhook Asaas: billing não encontrado para subscription={}, evento={}",
                    subscriptionId, payload.event());
            return;
        }

        var billing = billingOpt.get();
        BillingStatus statusBefore = billing.getStatus();
        boolean handled = applyEvent(payload, billing, paymentId);

        billingEventRepositoryPort.save(new BillingEventEntry(
                billing.getClinicId(),
                payload.event(),
                paymentId,
                subscriptionId,
                statusBefore != null ? statusBefore.name() : null,
                handled ? billing.getStatus().name() : null,
                rawPayload
        ));

        if (handled) {
            clinicBillingRepositoryPort.save(billing);
            log.info("Webhook Asaas processado: evento={} clinicId={} status {} → {}",
                    payload.event(), billing.getClinicId(), statusBefore, billing.getStatus());
        } else {
            log.info("Webhook Asaas: evento não mapeado={}, registrado em billing_events", payload.event());
        }
    }

    /**
     * Aplica a transição de status correspondente ao evento.
     * Retorna true se o evento foi tratado (billing deve ser salvo), false caso contrário.
     */
    private boolean applyEvent(AsaasWebhookPayload payload, ClinicBillingEntity billing, String paymentId) {
        switch (payload.event()) {
            case "PAYMENT_CREATED" -> {
                if (paymentId != null) billing.setLastPaymentId(paymentId);
                billing.setStatus(BillingStatus.PENDING);
            }
            case "PAYMENT_RECEIVED", "PAYMENT_CONFIRMED", "PAYMENT_DUNNING_RECEIVED" -> {
                billing.setStatus(BillingStatus.ACTIVE);
            }
            case "PAYMENT_OVERDUE" -> {
                billing.setStatus(BillingStatus.PAST_DUE);
            }
            case "PAYMENT_REFUNDED", "PAYMENT_DELETED" -> {
                // Cobrança estornada/deletada: volta a aguardar nova cobrança
                billing.setStatus(BillingStatus.PENDING);
            }
            case "SUBSCRIPTION_DELETED" -> {
                billing.setStatus(BillingStatus.CANCELED);
            }
            case "PAYMENT_AWAITING_RISK_ANALYSIS" -> {
                billing.setStatus(BillingStatus.PENDING);
            }
            default -> {
                return false;
            }
        }
        return true;
    }

    private String extractSubscriptionId(AsaasWebhookPayload payload) {
        // Eventos de pagamento: a assinatura vem dentro do objeto payment
        if (payload.payment() != null && payload.payment().subscription() != null) {
            return payload.payment().subscription();
        }
        // Eventos de assinatura (ex: SUBSCRIPTION_DELETED): a assinatura vem na raiz
        if (payload.subscription() != null && payload.subscription().id() != null) {
            return payload.subscription().id();
        }
        return null;
    }

    private static String serialize(AsaasWebhookPayload payload) {
        try {
            var sb = new StringBuilder();
            sb.append("{\"event\":\"").append(esc(payload.event())).append("\"");
            if (payload.payment() != null) {
                var p = payload.payment();
                sb.append(",\"payment\":{\"id\":\"").append(esc(p.id()))
                  .append("\",\"status\":\"").append(esc(p.status()))
                  .append("\",\"subscription\":\"").append(esc(p.subscription()))
                  .append("\"}");
            }
            if (payload.subscription() != null) {
                sb.append(",\"subscription\":{\"id\":\"")
                  .append(esc(payload.subscription().id()))
                  .append("\",\"status\":\"")
                  .append(esc(payload.subscription().status()))
                  .append("\"}");
            }
            sb.append("}");
            return sb.toString();
        } catch (Exception e) {
            return "{\"event\":\"" + payload.event() + "\"}";
        }
    }

    private static String esc(String s) {
        return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    // ─── Payload records ──────────────────────────────────────────────────────

    public record AsaasWebhookPayload(
            String event,
            AsaasWebhookPayment payment,
            AsaasWebhookSubscription subscription
    ) {}

    public record AsaasWebhookPayment(
            String id,
            String status,
            String subscription
    ) {}

    public record AsaasWebhookSubscription(
            String id,
            String status
    ) {}
}
