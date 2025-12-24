package com.guilinares.clinicai.flow.dto;

import java.util.Map;

public record FlowNluContext(
        String currentStep,
        String fieldKey,
        Map<String, Object> stateData,
        String lastUserMessage,
        String lastBotMessage
) {}
