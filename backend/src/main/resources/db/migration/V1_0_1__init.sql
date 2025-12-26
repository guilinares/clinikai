-- Extensão para UUID
create extension if not exists pgcrypto;

-- clinics
create table if not exists clinics (
  id uuid primary key default gen_random_uuid(),
  name varchar(150) not null,
  specialty varchar(150),
  whatsapp_number varchar(30),
  timezone varchar(50) not null default 'America/Sao_Paulo',
  ai_config jsonb not null default '{}'::jsonb,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create index if not exists idx_clinics_whatsapp_number on clinics (whatsapp_number);

-- patients
create table if not exists patients (
  id uuid primary key default gen_random_uuid(),
  clinic_id uuid not null references clinics(id) on delete cascade,
  phone varchar(30) not null,
  full_name varchar(150),
  email varchar(150),
  patient_type varchar(50),
  first_contact_at timestamptz,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  constraint uk_patients_clinic_phone unique (clinic_id, phone)
);

-- conversations
create table if not exists conversations (
  id uuid primary key default gen_random_uuid(),
  clinic_id uuid not null references clinics(id) on delete cascade,
  patient_id uuid not null references patients(id) on delete cascade,
  channel varchar(30) not null,
  status varchar(30) not null default 'OPEN',
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  constraint uk_conversations_open unique (clinic_id, patient_id, channel, status)
);

-- messages
create table if not exists messages (
  id uuid primary key default gen_random_uuid(),
  conversation_id uuid not null references conversations(id) on delete cascade,
  direction varchar(10) not null, -- IN | OUT
  text text not null,
  created_at timestamptz not null default now()
);

create index if not exists idx_messages_conversation_created_at on messages (conversation_id, created_at);

-- conversation_state (1 por conversation)
create table if not exists conversation_state (
  conversation_id uuid primary key references conversations(id) on delete cascade,
  state jsonb not null default '{}'::jsonb,
  updated_at timestamptz not null default now()
);

-- clinic_fields (schema/checklist por clínica)
create table if not exists clinic_fields (
  id uuid primary key default gen_random_uuid(),
  clinic_id uuid not null references clinics(id) on delete cascade,
  field_key varchar(50) not null,
  required boolean not null default true,
  prompt_question text not null,
  priority int not null default 100,
  extractor_hint text,
  enabled boolean not null default true,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  constraint uk_clinic_fields unique (clinic_id, field_key)
);

-- clinic_kb_entries
create table if not exists clinic_kb_entries (
  id uuid primary key default gen_random_uuid(),
  clinic_id uuid not null references clinics(id) on delete cascade,
  title varchar(200) not null,
  content text not null,
  category varchar(50),
  tags text[],
  enabled boolean not null default true,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create index if not exists idx_kb_clinic_enabled on clinic_kb_entries (clinic_id, enabled);
