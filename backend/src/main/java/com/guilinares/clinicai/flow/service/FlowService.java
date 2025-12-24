package com.guilinares.clinicai.flow.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guilinares.clinicai.ai.ClinicalNluService;
import com.guilinares.clinicai.ai.NluResult;
import com.guilinares.clinicai.clinic.ClinicEntity;
import com.guilinares.clinicai.clinic.ClinicRepository;
import com.guilinares.clinicai.clinic.service.ClinicKbAiService;
import com.guilinares.clinicai.flow.FlowStateEntity;
import com.guilinares.clinicai.flow.FlowStateRepository;
import com.guilinares.clinicai.flow.dto.AssistantReplyResponse;
import com.guilinares.clinicai.flow.dto.FlowNluContext;
import com.guilinares.clinicai.flow.dto.IncomingMessageRequest;
import com.guilinares.clinicai.onboarding.OnboardingStepEntity;
import com.guilinares.clinicai.onboarding.service.OnboardingConfigService;
import com.guilinares.clinicai.patient.PatientEntity;
import com.guilinares.clinicai.patient.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class FlowService {

    private static final String STEP_INITIAL = "INITIAL";
    private static final String STEP_NAME = "ASK_FULL_NAME";
    private static final String STEP_READY_FOR_SCHEDULING = "READY_FOR_SCHEDULING";

    private final FlowStateRepository flowStateRepository;
    private final ClinicRepository clinicRepository;
    private final PatientRepository patientRepository;
    private final ObjectMapper objectMapper;
    private final ClinicalNluService clinicalNluService;
    private final OnboardingConfigService onboardingConfigService;
    private final ClinicKbAiService clinicKbAiService;

    public FlowService(FlowStateRepository flowStateRepository,
                       ClinicRepository clinicRepository,
                       PatientRepository patientRepository,
                       ObjectMapper objectMapper,
                       ClinicalNluService clinicalNluService,
                       OnboardingConfigService onboardingConfigService,
                       ClinicKbAiService clinicKbAiService) {
        this.flowStateRepository = flowStateRepository;
        this.clinicRepository = clinicRepository;
        this.patientRepository = patientRepository;
        this.objectMapper = objectMapper;
        this.clinicalNluService = clinicalNluService;
        this.onboardingConfigService = onboardingConfigService;
        this.clinicKbAiService = clinicKbAiService;
    }

    @Transactional
    public AssistantReplyResponse handleIncomingMessage(UUID clinicId, IncomingMessageRequest request) {

        var clinic = clinicRepository.findById(clinicId);

        if (clinic.isEmpty()) throw new RuntimeException("Clinica não encontrada.");

        String normalizedPhone = normalizePhone(request.getPhone());
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        String incomingMessage = request.getText().getMessage() != null ? request.getText().getMessage().trim() : "";

        // 1. Garantir paciente
        PatientEntity patient = patientRepository.findByClinicIdAndPhone(clinic.get().getId(), normalizedPhone)
                .orElseGet(() -> {
                    PatientEntity p = new PatientEntity();
                    p.setClinic(clinic.get());
                    p.setFullName("Paciente WhatsApp");
                    p.setPhone(normalizedPhone);
                    p.setPatientType("NAO_INFORMADO");
                    p.setFirstContactAt(now);
                    p.setCreatedAt(now);
                    p.setUpdatedAt(now);
                    return patientRepository.save(p);
                });

        // 2. Estado do fluxo
        FlowStateEntity flowState = flowStateRepository
                .findByClinicIdAndPhone(clinic.get().getId(), normalizedPhone)
                .orElseGet(() -> {
                    FlowStateEntity f = new FlowStateEntity();
                    f.setClinic(clinic.get());
                    f.setPatient(patient);
                    f.setPhone(normalizedPhone);
                    f.setChannel("WHATSAPP");
                    f.setCurrentStep(STEP_INITIAL);
                    f.setActive(true);
                    f.setCreatedAt(now);
                    f.setUpdatedAt(now);
                    return f;
                });

        flowState.setPatient(patient);
        flowState.setLastUserMessage(incomingMessage);
        flowState.setLastMessageAt(now);

        String currentStep = flowState.getCurrentStep() != null ? flowState.getCurrentStep() : STEP_INITIAL;

        Map<String, Object> state = loadStateData(flowState); // você já tem isso

        FlowNluContext context = new FlowNluContext(
                currentStep,
                null,
                state,
                flowState.getLastUserMessage(),
                flowState.getLastBotMessage()
        );

        // NLU (já passa o step atual)
        NluResult nlu = clinicalNluService.analyzePatientMessage(incomingMessage, context);

        String reply = processFlowStep(flowState, patient, incomingMessage, clinic.get(), nlu);

        flowState.setLastBotMessage(reply);
        flowState.setUpdatedAt(now);

        flowState = flowStateRepository.save(flowState);
        patientRepository.save(patient);

        return new AssistantReplyResponse(
                flowState.getId(),
                patient.getId(),
                reply,
                flowState.getCurrentStep()
        );
    }

    private String processFlowStep(FlowStateEntity flowState,
                                   PatientEntity patient,
                                   String message,
                                   ClinicEntity clinic,
                                   NluResult nlu) {

        String currentStep = flowState.getCurrentStep();
        if (currentStep == null) currentStep = STEP_INITIAL;

        Map<String, Object> state = loadStateData(flowState);

        // 1) Ainda não começou: pegar primeiro step configurado
        if (STEP_INITIAL.equals(currentStep)) {
            return handleInitial(flowState, clinic, state);
        }

        // 2) Já terminou o onboarding
        if (STEP_READY_FOR_SCHEDULING.equals(currentStep)) {
            return handleReadyForScheduling(flowState, state, clinic, message, nlu);
        }

        // 3) Passo de onboarding configurado
        return handleOnboardingStep(flowState, patient, clinic, state, message, nlu);
    }

    private String handleInitial(FlowStateEntity flowState,
                                 ClinicEntity clinic,
                                 Map<String, Object> state) {

        var firstStepOpt = onboardingConfigService.getFirstStep(clinic);

//        if (firstStepOpt.isEmpty()) {
//            // Se não houver onboarding configurado, cai direto em scheduling
//            flowState.setCurrentStep(STEP_READY_FOR_SCHEDULING);
//            flowState.setStateData(writeStateData(state));
//            return """
//                    Olá! Sou a assistente virtual da %s 😊
//                    Já podemos te ajudar com o agendamento. Me diga qual o motivo principal da consulta.
//                    """.formatted(clinic.getName());
//        }

        OnboardingStepEntity first = firstStepOpt.get();
        flowState.setCurrentStep(first.getStepKey());
        flowState.setStateData(writeStateData(state));

        String question = onboardingConfigService.renderQuestion(first, state);

        return question;
    }

    private String handleOnboardingStep(FlowStateEntity flowState,
                                        PatientEntity patient,
                                        ClinicEntity clinic,
                                        Map<String, Object> state,
                                        String message,
                                        NluResult nlu) {

        String stepKey = flowState.getCurrentStep();

        OnboardingStepEntity currentStep = onboardingConfigService
                .getStep(clinic, stepKey)
                .orElse(null);

        if (currentStep == null) {
            // Se por algum motivo não achar o step, cai pra pronto pra agendar
            flowState.setCurrentStep(STEP_READY_FOR_SCHEDULING);
            flowState.setStateData(writeStateData(state));
            return """
                    Já coletamos as principais informações, vamos te ajudar com o agendamento na %s 😉
                    """.formatted(clinic.getName());
        }



        FlowNluContext fieldContext = new FlowNluContext(
                flowState.getCurrentStep(),
                currentStep.getFieldKey(),
                state,
                flowState.getLastUserMessage(),
                flowState.getLastBotMessage()
        );

        NluResult nluForField = clinicalNluService.analyzePatientMessage(
                message,
                fieldContext
        );

        if ("FAQ".equalsIgnoreCase(nluForField.getIntent())) {
            String faqQuery = nluForField.getEntity("faqQuery", String.class);
            if (faqQuery == null || faqQuery.isBlank()) faqQuery = message;

            String faqReply = buildFaqReply(clinic, message, nluForField);

            String question = onboardingConfigService.renderQuestion(currentStep, state);

            flowState.setStateData(writeStateData(state)); // mantém
            return faqReply + "\n\n" + question;
        }

        // 1) Salvar a resposta desse campo usando NLU + regras
        applyFieldValue(currentStep.getFieldKey(), message, nluForField, state, patient);

        // 2) Próximo passo
        var nextStepOpt = onboardingConfigService.getNextStep(clinic, stepKey);

        if (nextStepOpt.isEmpty()) {
            // Não há próximo passo → pronto para agendar
            flowState.setCurrentStep(STEP_READY_FOR_SCHEDULING);
            flowState.setStateData(writeStateData(state));

            return """
                    Perfeito, já tenho tudo que preciso aqui 😊
                    Já vou te ajudar com as opções de horários para agendamento.
                    """;
        }

        OnboardingStepEntity nextStep = nextStepOpt.get();
        flowState.setCurrentStep(nextStep.getStepKey());
        flowState.setStateData(writeStateData(state));

        return onboardingConfigService.renderQuestion(nextStep, state);
    }

    private void applyFieldValue(String fieldKey,
                                 String rawMessage,
                                 NluResult nlu,
                                 Map<String, Object> state,
                                 PatientEntity patient) {

        if (fieldKey == null) return;

        switch (fieldKey) {
            case "name" -> {
                String name = rawMessage != null ? rawMessage.trim() : null;
                if (name != null && !name.isBlank()) {
                    state.put("name", name);
                    patient.setFullName(name);
                }
            }

            case "age" -> {
                // aqui poderíamos pedir pra IA tentar extrair um número de idade
                String ageText = rawMessage != null ? rawMessage.trim() : null;
                state.put("age", ageText);
            }

            case "email" -> {
                String email = rawMessage != null ? rawMessage.trim() : null;
                state.put("email", email);
                patient.setEmail(email);
            }

            case "reason" -> {
                String reason = rawMessage != null ? rawMessage.trim() : null;
                state.put("reason", reason);
            }

            case "patientType" -> {
                String patientType = nlu.getEntity("patientType", String.class);
                String patientTypeRaw = nlu.getEntity("patientTypeRaw", String.class);
                if (patientTypeRaw == null) patientTypeRaw = rawMessage;
                if (patientType == null) patientType = "NAO_INFORMADO";

                state.put("patientTypeRaw", patientTypeRaw);
                state.put("patientType", patientType);
                patient.setPatientType(patientType);
            }

            case "urgent" -> {
                Boolean urgent = nlu.getEntity("urgent", Boolean.class);
                if (urgent == null) urgent = false;
                state.put("urgent", urgent);
                state.put("urgentRaw", rawMessage);
            }

            case "preferredDays" -> {
                String preferredDays = nlu.getEntity("preferredDays", String.class);
                String preferredWeek = nlu.getEntity("preferredWeek", String.class);
                String preferredPeriod = nlu.getEntity("preferredPeriod", String.class);

                if (preferredDays == null) preferredDays = rawMessage;
                if (preferredWeek == null) preferredWeek = "UNSPECIFIED";
                if (preferredPeriod == null) preferredPeriod = "UNSPECIFIED";

                state.put("preferredDays", preferredDays);
                state.put("preferredWeek", preferredWeek);
                state.put("preferredPeriod", preferredPeriod);
            }

            default -> {
                // Campo genérico → guarda no state_data
                String value = rawMessage != null ? rawMessage.trim() : null;
                state.put(fieldKey, value);
            }
        }
    }

    private String handleReadyForScheduling(FlowStateEntity flowState,
                                            Map<String, Object> state,
                                            ClinicEntity clinic,
                                            String message,
                                            NluResult nlu) {

        if (message != null && !message.isBlank()) {
            state.put("lastPreferenceExtra", message);
        }

        flowState.setStateData(writeStateData(state));

        return """
                Obrigado pelas informações! 🙌
                Já temos tudo que precisamos para te ajudar com o agendamento na %s.
                Em instantes vamos te enviar opções de horários.
                """.formatted(clinic.getName());
    }

    // helpers

    private String normalizePhone(String phone) {
        if (phone == null) return null;
        return phone.replaceAll("\\s+", "")
                .replaceAll("-", "");
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> loadStateData(FlowStateEntity flowState) {
        if (flowState.getStateData() == null || flowState.getStateData().isBlank()) {
            return new HashMap<>();
        }
        try {
            return objectMapper.readValue(flowState.getStateData(), Map.class);
        } catch (JsonProcessingException e) {
            return new HashMap<>();
        }
    }

    private String writeStateData(Map<String, Object> state) {
        try {
            return objectMapper.writeValueAsString(state);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }

    private String buildFaqReply(ClinicEntity clinic, String originalMessage, NluResult nlu) {

        // Se veio multi-faq
        var faqItems = nlu.getListEntity("faqItems", Map.class);

        if (faqItems != null && !faqItems.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (Object obj : faqItems) {
                Map<String, Object> item = (Map<String, Object>) obj;
                String q = item.get("query") != null ? item.get("query").toString() : originalMessage;

                String answer = clinicKbAiService.answer(clinic, q);

                if (sb.length() > 0) sb.append("\n");
                sb.append(answer);
            }
            return sb.toString();
        }

        // fallback: pergunta única (mensagem inteira)
        return clinicKbAiService.answer(clinic, originalMessage);
    }

}
