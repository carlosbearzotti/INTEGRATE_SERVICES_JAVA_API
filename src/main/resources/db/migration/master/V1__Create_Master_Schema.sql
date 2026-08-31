CREATE TABLE IF NOT EXISTS consumers (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    schema_name VARCHAR(63) NOT NULL UNIQUE,
    api_key VARCHAR(255) NOT NULL UNIQUE,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Inserir o primeiro consumer (Fintech Startup) por padrão
INSERT INTO consumers (name, schema_name, api_key, status, created_at)
SELECT 'Fintech Startup', 'tenant_fintech', 'fintech-startup-key-12345', 'ACTIVE', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM consumers WHERE schema_name = 'tenant_fintech');
