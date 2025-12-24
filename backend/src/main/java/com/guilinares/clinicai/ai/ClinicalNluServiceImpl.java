package com.guilinares.clinicai.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.guilinares.clinicai.ai.clients.OpenAiClient;
import com.guilinares.clinicai.clinic.ClinicEntity;
import com.guilinares.clinicai.flow.dto.FlowNluContext;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

@Service
public class ClinicalNluServiceImpl implements ClinicalNluService {

    private final OpenAiClient openAiClient;

    public ClinicalNluServiceImpl(OpenAiClient openAiClient) {
        this.openAiClient = openAiClient;
    }

    @Override
    public NluResult analyzePatientMessage(String message, FlowNluContext context) {

        if (message == null || message.isBlank()) {
            return new NluResult("UNKNOWN", Map.of());
        }

        String systemPrompt = buildSystemPrompt();
        String userPrompt = buildUserPrompt(message, context);

        JsonNode json = openAiClient.chatAsJson(systemPrompt, userPrompt);

        String intent = json.path("intent").asText("UNKNOWN");
        JsonNode entitiesNode = json.path("entities");
        Map<String, Object> entities = new HashMap<>();

        if (entitiesNode.isObject()) {
            entitiesNode.fieldNames().forEachRemaining(field -> {
                entities.put(field, convertJsonNode(entitiesNode.get(field)));
            });
        }

        return new NluResult(intent, entities);
    }

    @Override
    public String answerFromKb(String kbContext, String userQuestion) {

        String systemPrompt = """
                Você responde dúvidas de pacientes de uma clínica.
                Use SOMENTE o conteúdo do KB fornecido. Se a resposta não estiver no KB, diga que não tem essa informação no momento e ofereça encaminhar para a equipe/avaliação.
                Responda curto, em português do Brasil, acolhedor. Apenas um JSON no seguinte formato:
                
                {
                  "answer": "Resposta para a pergunta"
                }
                """;
        String userPrompt = """
                KB:
                %s
                
                Pergunta do paciente:
                %s
                """.formatted(kbContext, userQuestion);

        JsonNode json = openAiClient.chatAsJson(systemPrompt, userPrompt);

        return json.path("answer").asText("UNKNOWN");
    }

    private String buildSystemPrompt() {
        return """
            Você é uma IA especializada em atendimento de consultórios via WhatsApp.

            Sua função é interpretar a mensagem do paciente dentro do CONTEXTO da conversa
            e devolver APENAS um JSON no formato:

            {
              "intent": "NOME_DA_INTENCAO",
              "entities": { ... }
            }

            Intenções possíveis:
            - SAUDACAO
            - ANSWER_FIELD (quando o paciente está respondendo o fieldKey atual)
            - FAQ (quando o paciente faz uma ou mais perguntas/dúvidas)
            - CORRECAO_DADO
            - MUDANCA_INTENCAO
            - CANCELAR_ATENDIMENTO
            - REMARCAR
            - OUTRA

            Regras IMPORTANTES:
            - Use SEMPRE o contexto (currentStep, fieldKey, state_data, última msg do bot).
            - Se a mensagem tiver uma ou mais PERGUNTAS (ex: preço, convênio, procedimentos, endereço, horários),
              use intent "FAQ" e retorne entities.faqItems como uma lista.
            - Cada faqItem deve ter:
              - topic: uma categoria curta (ex: PRICE, INSURANCE, PROCEDURE, LOCATION, SCHEDULING, HOURS, OTHER)
              - query: a pergunta reescrita de forma curta
            - Se houver múltiplas perguntas na mesma mensagem, retorne múltiplos itens em faqItems.
            - Se estiver coletando um fieldKey e o paciente perguntar algo, priorize FAQ (não invente resposta).
            - Não explique nada.
            - Não inclua texto fora do JSON.
            - Todos os textos em português do Brasil.

            Exemplos:

            1) Mensagem: "Sou SulAmérica. Qual o valor?"
            Resposta:
            {
              "intent": "FAQ",
              "entities": {
                "faqItems": [
                  {"topic": "INSURANCE", "query": "Vocês atendem SulAmérica?"},
                  {"topic": "PRICE", "query": "Qual o valor da consulta?"}
                ]
              }
            }

            2) Mensagem: "Tenho 28"
            Resposta:
            {
              "intent": "ANSWER_FIELD",
              "entities": {
                "age": 28
              }
            }
            """;
    }


    private String buildUserPrompt(String message, FlowNluContext context) {

        String currentStep = context != null ? context.currentStep() : null;
        String fieldKey    = context != null ? context.fieldKey()    : null;
        Map<String, Object> stateData = context != null ? context.stateData() : null;
        String lastUser    = context != null ? context.lastUserMessage() : null;
        String lastBot     = context != null ? context.lastBotMessage()  : null;

        String stateSummary = buildStateSummary(stateData);
        String fieldSpecificPart = buildFieldSpecificPart(fieldKey);

        String lastMessagesBlock = "";

        if ((lastUser != null && !lastUser.isBlank()) ||
                (lastBot  != null && !lastBot.isBlank())) {

            lastMessagesBlock = """
                    Histórico imediato da conversa:

                    - Última mensagem do BOT: "%s"
                    - Última mensagem do PACIENTE: "%s"

                    Use isso para entender se a mensagem atual é resposta,
                    correção ou mudança de ideia.
                    """.formatted(
                    Objects.toString(lastBot, ""),
                    Objects.toString(lastUser, "")
            );
        }

        return """
                Contexto do fluxo:

                - Etapa atual (currentStep): %s
                - Campo que estamos coletando (fieldKey): %s

                Dados já coletados do paciente (state_data resumido):
                %s

                %s

                Mensagem ATUAL do paciente:
                "%s"

                %s

                Retorne APENAS um JSON, por exemplo:
                {
                  "intent": "INFORM_REASON",
                  "entities": {
                    "reason": "Tratamento de acne no rosto"
                  }
                }
                """.formatted(
                Objects.toString(currentStep, "UNKNOWN"),
                Objects.toString(fieldKey, "NONE"),
                stateSummary,
                fieldSpecificPart,
                message,
                lastMessagesBlock
        );
    }

    private String buildStateSummary(Map<String, Object> stateData) {
        if (stateData == null || stateData.isEmpty()) {
            return "- (ainda não há dados relevantes salvos)";
        }

        StringBuilder sb = new StringBuilder();
        stateData.forEach((key, value) -> {
            sb.append(String.format("- %s: ", key)).append(value.toString()).append("\n");
        });

        return sb.toString();
    }

    private String buildFieldSpecificPart(String fieldKey) {
        if (fieldKey == null || fieldKey.isBlank()) {
            return "Não existe fieldKey. Classifique em FAQ, CANCEL ou OTHER.";
        }

        return """
    O fieldKey atual é "%s".

    Decida:
    - Se a mensagem responde esse fieldKey, retorne intent=ANSWER_FIELD e em entities inclua { "%s": <valor extraído> }.
    - Se a mensagem for uma dúvida/interrupção (preço, procedimentos, etc), retorne intent=FAQ e inclua entities.faqQuery e entities.faqTopic.
    """.formatted(fieldKey, fieldKey);
    }

    private Object convertJsonNode(JsonNode node) {
        if (node == null || node.isNull()) return null;

        if (node.isBoolean()) return node.asBoolean();
        if (node.isInt()) return node.asInt();
        if (node.isLong()) return node.asLong();
        if (node.isFloatingPointNumber()) return node.asDouble();
        if (node.isTextual()) return node.asText();

        if (node.isArray()) {
            var list = new java.util.ArrayList<>();
            for (JsonNode item : node) {
                list.add(convertJsonNode(item));
            }
            return list;
        }

        if (node.isObject()) {
            var map = new java.util.HashMap<String, Object>();
            node.fieldNames().forEachRemaining(f -> map.put(f, convertJsonNode(node.get(f))));
            return map;
        }

        return node.toString();
    }

}
