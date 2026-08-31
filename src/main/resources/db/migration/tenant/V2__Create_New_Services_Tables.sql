-- =============================================================================
-- V2: NOVOS MICROSSERVIÇOS & DOMÍNIOS NO SCHEMA DO TENANT
-- =============================================================================

-- 1. Catálogo e Posições de Investimentos
CREATE TABLE IF NOT EXISTS tb_investments (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    type VARCHAR(50) NOT NULL, -- CDB, LCI, LCA, TESOURO
    index_name VARCHAR(50) NOT NULL, -- CDI, SELIC, IPCA, PREFIXADO
    rate_percent DOUBLE PRECISION NOT NULL, -- Ex: 120.0 para 120% do CDI
    min_amount DOUBLE PRECISION NOT NULL DEFAULT 100.0,
    liquidity VARCHAR(50) NOT NULL DEFAULT 'DIARIA',
    grace_period_days INTEGER NOT NULL DEFAULT 0,
    ir_exempt BOOLEAN NOT NULL DEFAULT FALSE,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS tb_investment_positions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    product_name VARCHAR(150) NOT NULL,
    product_type VARCHAR(50) NOT NULL,
    principal_amount DOUBLE PRECISION NOT NULL,
    current_amount DOUBLE PRECISION NOT NULL,
    rate_percent DOUBLE PRECISION NOT NULL,
    ir_exempt BOOLEAN NOT NULL DEFAULT FALSE,
    applied_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    maturity_date TIMESTAMP,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE' -- ACTIVE, REDEEMED
);

-- Seed de Produtos de Renda Fixa
INSERT INTO tb_investments (name, type, index_name, rate_percent, min_amount, liquidity, grace_period_days, ir_exempt, active)
SELECT 'CDB Turbinado LãoBank 120% CDI', 'CDB', 'CDI', 120.0, 100.00, 'DIARIA', 0, FALSE, TRUE
WHERE NOT EXISTS (SELECT 1 FROM tb_investments WHERE name = 'CDB Turbinado LãoBank 120% CDI');

INSERT INTO tb_investments (name, type, index_name, rate_percent, min_amount, liquidity, grace_period_days, ir_exempt, active)
SELECT 'LCI Imobiliário Prime 95% CDI', 'LCI', 'CDI', 95.0, 500.00, '90_DIAS', 90, TRUE, TRUE
WHERE NOT EXISTS (SELECT 1 FROM tb_investments WHERE name = 'LCI Imobiliário Prime 95% CDI');

INSERT INTO tb_investments (name, type, index_name, rate_percent, min_amount, liquidity, grace_period_days, ir_exempt, active)
SELECT 'Tesouro Selic 2029', 'TESOURO', 'SELIC', 100.0, 50.00, 'D+1', 1, FALSE, TRUE
WHERE NOT EXISTS (SELECT 1 FROM tb_investments WHERE name = 'Tesouro Selic 2029');


-- 2. Motor Antifraude (Fraud Shield)
CREATE TABLE IF NOT EXISTS tb_fraud_alerts (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    transaction_amount DOUBLE PRECISION NOT NULL,
    origin_lat DOUBLE PRECISION,
    origin_lng DOUBLE PRECISION,
    user_lat DOUBLE PRECISION,
    user_lng DOUBLE PRECISION,
    distance_km DOUBLE PRECISION,
    risk_score INTEGER NOT NULL, -- 0 a 100
    reason VARCHAR(500) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'UNDER_REVIEW', -- UNDER_REVIEW, APPROVED, REJECTED
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    reviewed_at TIMESTAMP,
    reviewed_by VARCHAR(150)
);

CREATE TABLE IF NOT EXISTS tb_fraud_rules (
    id BIGSERIAL PRIMARY KEY,
    rule_name VARCHAR(150) NOT NULL,
    max_distance_km DOUBLE PRECISION NOT NULL DEFAULT 500.0,
    max_amount DOUBLE PRECISION NOT NULL DEFAULT 50000.0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

-- Seed de Regra Padrão
INSERT INTO tb_fraud_rules (rule_name, max_distance_km, max_amount, is_active)
SELECT 'Regra Geral de Geofencing e Limite Noturno', 500.0, 50000.0, TRUE
WHERE NOT EXISTS (SELECT 1 FROM tb_fraud_rules WHERE rule_name = 'Regra Geral de Geofencing e Limite Noturno');


-- 3. Gateway de Webhooks & Eventos Assíncronos
CREATE TABLE IF NOT EXISTS tb_webhooks (
    id BIGSERIAL PRIMARY KEY,
    url VARCHAR(1024) NOT NULL,
    event_types VARCHAR(500) NOT NULL, -- Separado por vírgulas ex: 'transaction.completed,loan.approved'
    secret_key VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS tb_webhook_deliveries (
    id BIGSERIAL PRIMARY KEY,
    webhook_id BIGINT REFERENCES tb_webhooks(id) ON DELETE CASCADE,
    event_type VARCHAR(100) NOT NULL,
    payload TEXT NOT NULL,
    status_code INTEGER,
    signature VARCHAR(255),
    response_body TEXT,
    success BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);


-- 4. Transferências Agendadas (@Scheduled)
CREATE TABLE IF NOT EXISTS tb_scheduled_transfers (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    recipient_name VARCHAR(255) NOT NULL,
    recipient_document VARCHAR(100) NOT NULL,
    amount DOUBLE PRECISION NOT NULL,
    transfer_type VARCHAR(50) NOT NULL DEFAULT 'PIX', -- PIX, TED, BOLETO
    scheduled_for DATE NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'SCHEDULED', -- SCHEDULED, EXECUTED, CANCELLED, FAILED
    executed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);


-- 5. Auditoria & Compliance LGPD
CREATE TABLE IF NOT EXISTS tb_audit_logs (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    action VARCHAR(100) NOT NULL,
    entity_name VARCHAR(100),
    entity_id VARCHAR(100),
    ip_address VARCHAR(100),
    user_agent VARCHAR(255),
    details TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
