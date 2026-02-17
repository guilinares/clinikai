alter table clinics
  add column if not exists billing_document varchar(14);

-- Garante que não haverá espaços ou pontuação
-- (a normalização deve ser feita no backend também)
comment on column clinics.billing_document
  is 'CPF ou CNPJ da clínica (somente números), usado exclusivamente para cobrança';
