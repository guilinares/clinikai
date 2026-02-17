package com.guilinares.clinikai.presentation.controllers.dto;

import java.time.OffsetDateTime;

public record ApiErrorResponse(
        String code,
        String message,
        String action,
        OffsetDateTime timestamp
) {
    public static ApiErrorResponse of(String code, String message, String action) {
        return new ApiErrorResponse(code, message, action, OffsetDateTime.now());
    }
}