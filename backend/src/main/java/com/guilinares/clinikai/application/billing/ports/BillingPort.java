package com.guilinares.clinikai.application.billing.ports;

import com.guilinares.clinikai.infrastructure.billing.dto.*;

public interface BillingPort {
    AsaasCustomerResponse createCustomer(AsaasCustomerCreateRequest req);
    AsaasSubscriptionResponse createSubscription(AsaasSubscriptionCreateRequest req);
    AsaasPixQrCodeResponse getPixOrQrCode(String paymentId);


}
