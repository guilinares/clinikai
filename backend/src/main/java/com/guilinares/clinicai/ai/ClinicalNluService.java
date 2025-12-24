package com.guilinares.clinicai.ai;

import com.guilinares.clinicai.clinic.ClinicEntity;
import com.guilinares.clinicai.flow.dto.FlowNluContext;

import java.util.Map;

public interface ClinicalNluService {

    /**
     * Analisa uma mensagem de paciente no contexto do fluxo.
     *
     * @param message     mensagem em linguagem natural
     * @param context     contexto atual da mensagem
     */
    NluResult analyzePatientMessage(String message, FlowNluContext context);
    String answerFromKb(String kbContext, String userQuestion);

    /**
     * Resultado bruto (útil para debug ou logs se quiser).
     */
    default NluResult ofRaw(String intent, Map<String, Object> entities) {
        return new NluResult(intent, entities);
    }
}
