-- V15__create_clinic_billing.sql

-- Tabela de billing da clínica (MVP Asaas + BASIC + Pix)
-- provider: ASAAS
-- plan: BASIC
-- status: NO_SUBSCRIPTION | PENDING | ACTIVE | PAST_DUE | CANCELED

create table if not exists clinic_billing (
  clinic_id uuid primary key
    references clinics(id) on delete cascade,

  provider varchar(20) not null default 'ASAAS',
  plan varchar(20) not null default 'BASIC',
  status varchar(30) not null default 'NO_SUBSCRIPTION',

  asaas_customer_id varchar(60),
  asaas_subscription_id varchar(60),

  -- cobrança atual
  last_payment_id varchar(60),

  -- cache opcional do Pix
  last_pix_payload text,
  last_pix_encoded_image text,
  last_pix_expires_at timestamptz,

  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

-- índices úteis
create index if not exists idx_clinic_billing_status
  on clinic_billing(status);

create index if not exists idx_clinic_billing_customer
  on clinic_billing(asaas_customer_id);

create index if not exists idx_clinic_billing_subscription
  on clinic_billing(asaas_subscription_id);

create index if not exists idx_clinic_billing_last_payment
  on clinic_billing(last_payment_id);

-- trigger para updated_at
create or replace function set_clinic_billing_updated_at()
returns trigger as $$
begin
  new.updated_at = now();
  return new;
end;
$$ language plpgsql;

drop trigger if exists trg_clinic_billing_updated_at on clinic_billing;

create trigger trg_clinic_billing_updated_at
before update on clinic_billing
for each row
execute function set_clinic_billing_updated_at();
