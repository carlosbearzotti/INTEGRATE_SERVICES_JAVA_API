-- =============================================================================
-- V1: SCHEMA MASTER & TABELAS PRINCIPAIS DO LÃOBANK
-- =============================================================================

CREATE TABLE IF NOT EXISTS consumers (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    schema_name VARCHAR(63) NOT NULL UNIQUE,
    api_key VARCHAR(255) NOT NULL UNIQUE,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Inserir o LãoBank como consumer principal
INSERT INTO consumers (name, schema_name, api_key, status, created_at)
SELECT 'LãoBank Digital', 'public', 'laobank-digital-key-99999', 'ACTIVE', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM consumers WHERE api_key = 'laobank-digital-key-99999');

-- Tabela de Usuários (Correntistas e Colaboradores LãoBank)
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    cpf VARCHAR(255) NOT NULL UNIQUE,
    income DOUBLE PRECISION NOT NULL,
    age INTEGER NOT NULL,
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    role VARCHAR(50) NOT NULL DEFAULT 'ROLE_CUSTOMER'
);

-- Seed da Conta de Administrador Real (Carlos Bearzotti)
INSERT INTO users (name, email, password, cpf, income, age, latitude, longitude, role)
SELECT 'Carlos Bearzotti (Admin)', 'admin@laobank.com.br', '$2b$10$dbfgT5w6ZSi1Yqldb2VSK.EFoUx2.2z6sD9ijsX3I7RP8b2XbvVDe', '000.000.000-01', 30000.00, 38, -23.5505, -46.6333, 'ROLE_ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'admin@laobank.com.br');

-- Tabela de Transações com Criptografia
CREATE TABLE IF NOT EXISTS transactions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    user_document VARCHAR(512),
    credit_card_token VARCHAR(2048),
    transaction_value BIGINT
);

-- Tabela de Pontos de Interesse (GPS / Agências e Caixas LãoBank)
CREATE TABLE IF NOT EXISTS tb_pois (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    x INTEGER NOT NULL,
    y INTEGER NOT NULL
);

-- Seeds de Caixas e Agências LãoBank
INSERT INTO tb_pois (name, x, y)
SELECT 'Agência Central LãoBank - Av. Paulista', 20, 10
WHERE NOT EXISTS (SELECT 1 FROM tb_pois WHERE name = 'Agência Central LãoBank - Av. Paulista');

INSERT INTO tb_pois (name, x, y)
SELECT 'Caixa 24h LãoBank - Shopping Morumbi', 35, 15
WHERE NOT EXISTS (SELECT 1 FROM tb_pois WHERE name = 'Caixa 24h LãoBank - Shopping Morumbi');

INSERT INTO tb_pois (name, x, y)
SELECT 'Agência Premium LãoBank - Faria Lima', 10, 8
WHERE NOT EXISTS (SELECT 1 FROM tb_pois WHERE name = 'Agência Premium LãoBank - Faria Lima');

-- Tabela de Encurtador de URLs
CREATE TABLE IF NOT EXISTS tb_url_mappings (
    id BIGSERIAL PRIMARY KEY,
    short_code VARCHAR(10) NOT NULL UNIQUE,
    original_url VARCHAR(2048) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    access_count BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_url_mapping_short_code ON tb_url_mappings (short_code);
