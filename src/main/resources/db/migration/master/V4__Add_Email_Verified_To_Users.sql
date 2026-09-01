-- =============================================================================
-- V4: ADICIONA CONTROLE DE CONFIRMAÇÃO DE E-MAIL (EMAIL_VERIFIED)
-- =============================================================================

ALTER TABLE users ADD COLUMN IF NOT EXISTS email_verified BOOLEAN NOT NULL DEFAULT FALSE;

-- Garantir que contas administrativas já existentes permaneçam ativas
UPDATE users SET email_verified = TRUE WHERE role = 'ROLE_ADMIN';
