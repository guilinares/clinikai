create table if not exists users (
  id uuid primary key default gen_random_uuid(),
  clinic_id uuid not null references clinics(id),
  name varchar(150) not null,
  email varchar(150) not null,
  password_hash varchar(255) not null,
  role varchar(30) not null,
  created_at timestamptz not null,
  updated_at timestamptz not null
);

create unique index if not exists uk_users_email on users(email);
create index if not exists idx_users_clinic on users(clinic_id);
