package com.guilinares.clinicai.flow.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class AssistantReplyResponse {

    private UUID flowStateId;
    private UUID patientId;
    private String replyMessage;
    private String currentStep;
}
