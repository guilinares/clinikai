package com.guilinares.clinikai.presentation.controllers.dto;

import com.guilinares.clinikai.domain.enums.MessageDirection;

public record RegisterMessageRequest(String clinicId, String patientId, String message, MessageDirection direction) {
}
