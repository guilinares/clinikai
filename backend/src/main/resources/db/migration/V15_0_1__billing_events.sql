-- Tabela de histórico/auditoria de eventos de billing recebidos do Asaas

create table if not exists billing_events (
    id              uuid primary key default gen_random_uuid(),
    clinic_id       uuid not null references clinics(id) on delete cascade,
    event_type      varchar(60) not null,
    payment_id      varchar(60),
    subscription_id varchar(60),
    status_before   varchar(30),
    status_after    varchar(30),
    raw_payload     text,
    received_at     timestamptz not null default now()
);

create index idx_billing_events_clinic        on billing_events(clinic_id);
create index idx_billing_events_event_type    on billing_events(event_type);
create index idx_billing_events_received_at   on billing_events(received_at desc);
create index idx_billing_events_subscription  on billing_events(subscription_id);
