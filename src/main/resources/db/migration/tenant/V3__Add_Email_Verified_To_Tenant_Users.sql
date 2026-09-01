-- =============================================================================
-- V3: ADICIONA CONTROLE DE CONFIRMAÇÃO DE E-MAIL NO SCHEMA DO TENANT
-- =============================================================================

ALTER TABLE users ADD COLUMN IF NOT EXISTS email_verified BOOLEAN NOT NULL DEFAULT FALSE;

-- Garantir que contas administrativas do tenant permaneçam ativas
UPDATE users SET email_verified = TRUE WHERE role = 'ROLE_ADMIN';
