package com.guilinares.clinicai.onboarding.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guilinares.clinicai.ai.clients.OpenAiClient;
import com.guilinares.clinicai.clinic.ClinicEntity;
import com.guilinares.clinicai.onboarding.OnboardingStepEntity;
import com.guilinares.clinicai.onboarding.OnboardingStepRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OnboardingWizardService {

    private final OnboardingStepRepository onboardingStepRepository;
    private final OpenAiClient openAiClient;
    private final ObjectMapper objectMapper;

    @Transactional
    public List<OnboardingStepEntity> generateOnboardingWithAi(ClinicEntity clinic,
                                                               String description,
                                                               boolean replaceExisting) {

        String systemPrompt = """
                Você é uma IA que ajuda a definir fluxos de onboarding para atendimento de clínicas médicas via WhatsApp.
                Sua tarefa é transformar uma descrição em linguagem natural de um fluxo de perguntas
                em uma lista de passos estruturados.

                Você deve devolver APENAS um JSON no formato:

                {
                  "steps": [
                    {
                      "stepKey": "ASK_NAME",
                      "fieldKey": "name",
                      "question": "Texto da pergunta ao paciente",
                      "orderIndex": 1,
                      "required": true
                    }
                  ]
                }
                
                ATENÇÃO: você deve sempre perguntar o nome, idade e email, pois elas são necessárias para o cadastro do paciente no sistema.
                E para elas, voce deve retornar um mesmo nome de stepKey e fieldKey.
                
                - Para nome, stepKey = ASK_FULL_NAME e fieldKey = name
                - Para idade, stepKey = ASK_AGE e fieldKey = age
                - Para email, stepKey = ASK_EMAIL e fieldKey = email

                Regras:
                - stepKey deve ser um identificador em CAIXA_ALTA com underscore (ex.: ASK_FULL_NAME, ASK_AGE).
                - fieldKey deve ser em camelCase simples (ex.: name, age, reason, patientType, urgent, preferredDays).
                - question deve ser um texto amigável em português do Brasil.
                - orderIndex é a ordem do passo (1,2,3...).
                - required indica se o campo é obrigatório.
                - Não escreva nada fora do JSON.
                """;

        String userPrompt = """
                Descrição do fluxo de onboarding da clínica:

                "%s"

                Gere a lista de passos conforme o formato especificado.
                """.formatted(description);

        JsonNode json = openAiClient.chatAsJson(systemPrompt, userPrompt);
        JsonNode stepsNode = json.path("steps");

        if (!stepsNode.isArray()) {
            throw new IllegalStateException("Resposta da IA não contém 'steps' em array");
        }


        if (replaceExisting) {
            var onboardingSteps = onboardingStepRepository.findByClinicIdOrderByOrderIndexAsc(clinic.getId());
            for (var step : onboardingSteps) {
                onboardingStepRepository.deleteByIdAndClinicId(step.getId(), clinic.getId());
                onboardingStepRepository.flush();
            }
        }

        List<OnboardingStepEntity> result = new java.util.ArrayList<>();

        for (JsonNode stepNode : stepsNode) {
            String stepKey = stepNode.path("stepKey").asText();
            String fieldKey = stepNode.path("fieldKey").asText();
            String question = stepNode.path("question").asText();
            int orderIndex = stepNode.path("orderIndex").asInt();
            boolean required = stepNode.path("required").asBoolean(true);

            if (stepKey == null || stepKey.isBlank() ||
                    fieldKey == null || fieldKey.isBlank() ||
                    question == null || question.isBlank()) {
                continue;
            }

            OnboardingStepEntity step = new OnboardingStepEntity();
            step.setClinic(clinic);
            step.setStepKey(stepKey);
            step.setFieldKey(fieldKey);
            step.setQuestion(question);
            step.setOrderIndex(orderIndex);
            step.setRequired(required);

            result.add(onboardingStepRepository.save(step));
        }

        return result;
    }
}
