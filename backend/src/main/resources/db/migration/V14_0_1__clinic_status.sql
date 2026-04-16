-- Adiciona campo status à tabela clinics para controle de aprovação
ALTER TABLE clinics ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE';

-- Clínicas existentes já estão ativas
-- Novas clínicas via onboarding entrarão como PENDING_APPROVAL
