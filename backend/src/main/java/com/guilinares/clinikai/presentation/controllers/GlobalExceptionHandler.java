package com.guilinares.clinikai.presentation.controllers;

import com.guilinares.clinikai.application.billing.exceptions.HandleBillingException;
import com.guilinares.clinikai.application.google.exceptions.EventoNaoEncontradoException;
import com.guilinares.clinikai.application.google.exceptions.HorarioIndisponivelException;
import com.guilinares.clinikai.application.clinic.exceptions.ClinicaNaoEncontradaException;
import com.guilinares.clinikai.application.clinic.exceptions.FailDeleteKbException;
import com.guilinares.clinikai.application.clinic.exceptions.InvalidCategoryException;
import com.guilinares.clinikai.application.clinic.exceptions.NotClinicKbFound;
import com.guilinares.clinikai.application.clinic.exceptions.TelefoneJaPossuiClinicaException;
import com.guilinares.clinikai.domain.exceptions.WhatsappSubscriptionRequiredException;
import com.guilinares.clinikai.infrastructure.billing.exceptions.AsaasWebhookNotSuportedEvent;
import com.guilinares.clinikai.presentation.controllers.dto.ApiError;
import com.guilinares.clinikai.presentation.controllers.dto.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ClinicaNaoEncontradaException.class)
    public ResponseEntity<ApiError> handleClinicaNaoEncontrada(ClinicaNaoEncontradaException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError(e.getMessage()));
    }

    @ExceptionHandler(TelefoneJaPossuiClinicaException.class)
    public ResponseEntity<ApiError> handleTelefoneJaPossuiClinicaException(TelefoneJaPossuiClinicaException e) {
        return ResponseEntity
                .badRequest()
                .body(new ApiError(e.getMessage()));
    }

    @ExceptionHandler(NotClinicKbFound.class)
    public ResponseEntity<ApiError> handleNotClinicKbFound(NotClinicKbFound e) {
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(InvalidCategoryException.class)
    public ResponseEntity<ApiError> handleInvalidCategoryException(InvalidCategoryException e) {
        return ResponseEntity.badRequest().body(new ApiError("Categoria informada inválida."));
    }

    @ExceptionHandler(FailDeleteKbException.class)
    public ResponseEntity<ApiError> handleFailDeleteKbException(FailDeleteKbException e) {
        return ResponseEntity.badRequest().body(new ApiError("Falha ao deletar KB."));
    }

    @ExceptionHandler(AsaasWebhookNotSuportedEvent.class)
    public ResponseEntity<Object> handleNotSupportedAsaasWebhookEvent(AsaasWebhookNotSuportedEvent e) {
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(HandleBillingException.class)
    public ResponseEntity<Object> handleNotSupportedAsaasWebhookEvent(HandleBillingException e) {
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(HorarioIndisponivelException.class)
    public ResponseEntity<ApiError> handleHorarioIndisponivel(HorarioIndisponivelException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiError(e.getMessage()));
    }

    @ExceptionHandler(EventoNaoEncontradoException.class)
    public ResponseEntity<ApiError> handleEventoNaoEncontrado(EventoNaoEncontradoException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError(e.getMessage()));
    }

    @ExceptionHandler(WhatsappSubscriptionRequiredException.class)
    public ResponseEntity<ApiErrorResponse> handleSubscriptionRequired(WhatsappSubscriptionRequiredException ex) {
        return ResponseEntity.status(HttpStatus.PRECONDITION_REQUIRED)
                .body(ApiErrorResponse.of(
                        "WHATSAPP_SUBSCRIPTION_REQUIRED",
                        "Sua instância do WhatsApp precisa ser assinada para continuar enviando mensagens.",
                        "SUBSCRIBE_INSTANCE"
                ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(new ApiError(e.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiError> handleIllegalState(IllegalStateException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiError(e.getMessage()));
    }
}
