create table google_calendar_integration (
    id uuid primary key,
    clinic_id uuid not null unique,
    refresh_token_encrypted text not null,
    access_token_encrypted text,
    access_token_expires_at timestamp,
    calendar_id varchar(255),
    connected_at timestamp,
    revoked_at timestamp
);