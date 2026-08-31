-- Tabela de Usuários
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    cpf VARCHAR(255) NOT NULL UNIQUE,
    income DOUBLE PRECISION NOT NULL,
    age INTEGER NOT NULL,
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION
);

-- Seed de Usuário Demo Padrão
INSERT INTO users (name, email, password, cpf, income, age, latitude, longitude)
SELECT 'Carlos Silva', 'carlos@exemplo.com', 'SenhaForte@2026!', '123.456.789-00', 7500.00, 29, -23.5505, -46.6333
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'carlos@exemplo.com');

-- Tabela de Transações com Criptografia
CREATE TABLE IF NOT EXISTS transactions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    user_document VARCHAR(512),
    credit_card_token VARCHAR(2048),
    transaction_value BIGINT
);

-- Tabela de Pontos de Interesse (GPS)
CREATE TABLE IF NOT EXISTS tb_pois (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    x INTEGER NOT NULL,
    y INTEGER NOT NULL
);

-- Tabela de Encurtador de URL
CREATE TABLE IF NOT EXISTS tb_url_mappings (
    id BIGSERIAL PRIMARY KEY,
    short_code VARCHAR(10) NOT NULL UNIQUE,
    original_url VARCHAR(2048) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    access_count BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_url_mapping_short_code ON tb_url_mappings (short_code);
