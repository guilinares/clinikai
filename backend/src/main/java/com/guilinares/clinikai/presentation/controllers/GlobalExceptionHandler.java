package com.guilinares.clinikai.presentation.controllers;

import com.guilinares.clinikai.application.clinic.exceptions.TelefoneJaPossuiClinicaException;
import com.guilinares.clinikai.presentation.controllers.dto.ApiError;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TelefoneJaPossuiClinicaException.class)
    public ResponseEntity<ApiError> handleTelefoneJaPossuiClinicaException(TelefoneJaPossuiClinicaException e) {
        return ResponseEntity
                .badRequest()
                .body(new ApiError(e.getMessage()));
    }
}
