-- V20260210_01__create_table_clinic_whatsapp.sql
-- PostgreSQL

CREATE TABLE IF NOT EXISTS clinic_whatsapp (
    id                  UUID PRIMARY KEY,
    clinic_id            UUID NOT NULL,

    provider            VARCHAR(30) NOT NULL DEFAULT 'ZAPI',

    -- Identificação/credenciais da instância no provedor (Z-API)
    instance_id          VARCHAR(120) NOT NULL,
    instance_token       TEXT NOT NULL,

    -- Base URL do provedor (útil se variar por região/cluster/ambiente)
    base_url             TEXT NOT NULL,

    -- Webhook (segurança/validação)
    webhook_url          TEXT NULL,
    webhook_secret       TEXT NULL,

    -- Controle de estado da conexão/uso
    status               VARCHAR(30) NOT NULL DEFAULT 'UNPROVISIONED',
    is_shared            BOOLEAN NOT NULL DEFAULT FALSE, -- sua instância “compartilhada” de testes

    -- Campos úteis pra observabilidade e suporte
    phone_e164           VARCHAR(20) NULL,              -- número conectado, ex: +5511999999999
    last_connection_at   TIMESTAMPTZ NULL,
    last_error_code      VARCHAR(60) NULL,
    last_error_message   TEXT NULL,

    -- Auditoria
    created_at           TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at           TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_clinic_whatsapp_clinic
        FOREIGN KEY (clinic_id) REFERENCES clinics (id) ON DELETE CASCADE,

    -- 1 registro por clínica (se futuramente quiser múltiplos canais por clínica, remova isso)
    CONSTRAINT uq_clinic_whatsapp_clinic UNIQUE (clinic_id),

    -- Evita duplicar a mesma instância no banco (muito comum em erro de provisionamento)
    CONSTRAINT uq_clinic_whatsapp_instance UNIQUE (provider, instance_id)
);

-- Índices pra consultas comuns
CREATE INDEX IF NOT EXISTS idx_clinic_whatsapp_clinic_id
    ON clinic_whatsapp (clinic_id);

CREATE INDEX IF NOT EXISTS idx_clinic_whatsapp_status
    ON clinic_whatsapp (status);

CREATE INDEX IF NOT EXISTS idx_clinic_whatsapp_is_shared
    ON clinic_whatsapp (is_shared);

-- Trigger para updated_at
CREATE OR REPLACE FUNCTION set_updated_at_clinic_whatsapp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_set_updated_at_clinic_whatsapp ON clinic_whatsapp;

CREATE TRIGGER trg_set_updated_at_clinic_whatsapp
BEFORE UPDATE ON clinic_whatsapp
FOR EACH ROW
EXECUTE FUNCTION set_updated_at_clinic_whatsapp();
