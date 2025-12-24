-- Habilita extensão para UUID aleatório
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ==============
-- TABELA CLINICS
-- ==============

CREATE TABLE clinics (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name            VARCHAR(150) NOT NULL,
    specialty       VARCHAR(150),
    whatsapp_number VARCHAR(30),
    timezone        VARCHAR(50) DEFAULT 'America/Sao_Paulo',
    ai_config       JSONB,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- =============
-- TABELA USERS
-- =============

CREATE TABLE users (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    clinic_id      UUID NOT NULL REFERENCES clinics(id),
    name           VARCHAR(150) NOT NULL,
    email          VARCHAR(200) NOT NULL UNIQUE,
    password_hash  TEXT NOT NULL,
    role           VARCHAR(30) NOT NULL, -- ADMIN, RECEPTION, DOCTOR
    created_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at     TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ===============
-- TABELA PATIENTS
-- ===============

CREATE TABLE patients (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    clinic_id        UUID NOT NULL REFERENCES clinics(id),
    full_name        VARCHAR(200) NOT NULL,
    phone            VARCHAR(30) NOT NULL,
    email            VARCHAR(200),
    first_contact_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    patient_type     VARCHAR(20),
    extra_data       JSONB,
    CONSTRAINT uk_patient_clinic_phone UNIQUE (clinic_id, phone)
);

-- =================
-- TABELA FLOWS_STATE
-- =================

CREATE TABLE flows_state (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    clinic_id     UUID NOT NULL REFERENCES clinics(id),
    patient_id    UUID REFERENCES patients(id),
    phone         VARCHAR(30) NOT NULL,
    current_flow  VARCHAR(100) NOT NULL,
    current_state VARCHAR(100) NOT NULL,
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uk_flows_state_clinic_phone UNIQUE (clinic_id, phone)
);

CREATE TABLE onboarding_steps (
    id          UUID PRIMARY KEY,
    clinic_id   UUID NOT NULL REFERENCES clinics(id),
    step_key    VARCHAR(50) NOT NULL,
    field_key   VARCHAR(50) NOT NULL,
    question    TEXT NOT NULL,
    order_index INT NOT NULL,
    required    BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT uk_onboarding_clinic_step_key UNIQUE (clinic_id, step_key)
);


-- ===========================
-- DADOS INICIAIS (CLÍNICA + ADMIN)
-- ===========================

-- Usando UUIDs fixos pra ficar fácil de referenciar depois se quiser
INSERT INTO clinics (id, name, specialty, whatsapp_number, timezone, ai_config)
VALUES (
    '11111111-1111-1111-1111-111111111111',
    'Clínica Demo',
    'Dermatologia',
    '+5500000000000',
    'America/Sao_Paulo',
    '{}'::jsonb
);

-- Senha: Admin@123 (bcrypt)
-- Gerado com bcrypt:
-- $2b$12$FBh4qfpFCNaNSO15lt8NvutPBuYl7SI9s1qc3VyPY5oeQoWXPk4kq
INSERT INTO users (id, clinic_id, name, email, password_hash, role)
VALUES (
    '22222222-2222-2222-2222-222222222222',
    '11111111-1111-1111-1111-111111111111',
    'Admin Demo',
    'admin@clinicai.com',
    '$2b$12$FBh4qfpFCNaNSO15lt8NvutPBuYl7SI9s1qc3VyPY5oeQoWXPk4kq',
    'ADMIN'
);
