package com.guilinares.clinikai.infrastructure.billing.dto;

public record AsaasCustomerCreateRequest(
        String name,
        String email,
        String mobilePhone,
        String cpfCnpj
) {}
