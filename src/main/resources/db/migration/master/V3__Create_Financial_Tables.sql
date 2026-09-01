-- =============================================================================
-- V3: TABELAS FINANCEIRAS (EMPRÉSTIMOS, PIX E CARTÕES)
-- =============================================================================

-- ==========================================
-- 1. EMPRÉSTIMOS (LOANS)
-- ==========================================
CREATE TABLE IF NOT EXISTS loans (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    loan_type VARCHAR(50) NOT NULL DEFAULT 'PERSONAL', -- PERSONAL, PAYROLL, GUARANTEE
    amount DOUBLE PRECISION NOT NULL,
    amount_with_interest DOUBLE PRECISION NOT NULL,
    installments INTEGER NOT NULL,
    installment_value DOUBLE PRECISION NOT NULL,
    first_installment_date DATE NOT NULL,
    total_interest DOUBLE PRECISION NOT NULL,
    iof DOUBLE PRECISION NOT NULL,
    rate DOUBLE PRECISION NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, PAID, DEFAULTED
    contracted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ==========================================
-- 2. PIX E TRANSFERÊNCIAS
-- ==========================================
CREATE TABLE IF NOT EXISTS pix_keys (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    key_value VARCHAR(255) NOT NULL UNIQUE,
    key_type VARCHAR(50) NOT NULL, -- CPF, EMAIL, PHONE, RANDOM
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS pix_transactions (
    id BIGSERIAL PRIMARY KEY,
    sender_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    receiver_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    sender_document VARCHAR(255),
    receiver_document VARCHAR(255),
    amount DOUBLE PRECISION NOT NULL,
    description VARCHAR(500),
    status VARCHAR(50) NOT NULL DEFAULT 'COMPLETED', -- PENDING, COMPLETED, FAILED, REFUNDED
    txid VARCHAR(255) UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ==========================================
-- 3. CARTÕES E FATURAS (CARDS & INVOICES)
-- ==========================================
CREATE TABLE IF NOT EXISTS cards (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name_on_card VARCHAR(255) NOT NULL,
    card_number VARCHAR(50) NOT NULL UNIQUE,
    valid_thru VARCHAR(10) NOT NULL,
    cvv VARCHAR(10) NOT NULL,
    card_type VARCHAR(50) NOT NULL DEFAULT 'PHYSICAL', -- PHYSICAL, VIRTUAL
    limit_amount DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS card_invoices (
    id BIGSERIAL PRIMARY KEY,
    card_id BIGINT REFERENCES cards(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    amount DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    status VARCHAR(50) NOT NULL DEFAULT 'OPEN', -- OPEN, PAID, OVERDUE
    due_date DATE NOT NULL,
    reference_month INTEGER NOT NULL,
    reference_year INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
