package com.guilinares.clinikai.infrastructure.billing.dto;

public record AsaasSubscriptionCreateRequest(
        String customer,
        String billingType,
        String cycle,
        Double value,
        String nextDueDate, // "yyyy-MM-dd"
        String description
) {}