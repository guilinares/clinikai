package com.guilinares.clinikai.domain.clinic;

import com.guilinares.clinikai.application.clinic.exceptions.InvalidCategoryException;

public enum ClinicKbCategory {

    // Informações básicas
    FAQ,
    LOCALIZACAO,
    CONTATO,
    HORARIO_FUNCIONAMENTO,

    // Atendimento
    AGENDAMENTO,
    CANCELAMENTO,
    REMARCACAO,
    CONFIRMACAO,
    POS_CONSULTA,

    // Negócio
    PRECO,
    CONVENIOS,
    FORMAS_PAGAMENTO,
    REEMBOLSO,

    // Regras e políticas
    POLITICAS,
    DOCUMENTOS,
    LGPD,
    TERMOS_USO,

    // Saúde / clínica
    TRIAGEM,
    PROCEDIMENTOS,
    EXAMES,
    ESPECIALIDADES,

    // Operacional
    FLUXO_ATENDIMENTO,   // playbooks do bot
    SCRIPT_ATENDIMENTO, // frases prontas
    COMUNICACAO,        // tom/estilo

    // Segurança / legal
    DISCLAMER,
    EMERGENCIA;


    public static ClinicKbCategory from(String value) {
        if (value == null || value.isBlank()) return null;

        try {
            return ClinicKbCategory.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidCategoryException(); // ou lança exception de negócio
        }
    }

    public static String nameFrom(String value) {
        if (value == null || value.isBlank()) return null;

        try {
            return ClinicKbCategory.valueOf(value.trim().toUpperCase()).name();
        } catch (IllegalArgumentException e) {
            return null; // ou lança exception de negócio
        }
    }
}
