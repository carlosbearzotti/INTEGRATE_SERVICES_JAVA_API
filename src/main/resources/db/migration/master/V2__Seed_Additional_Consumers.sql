-- Inserir consumers adicionais para demonstrar o portal B2B Multi-Tenant
INSERT INTO consumers (name, schema_name, api_key, status, created_at)
SELECT 'LãoBank Digital', 'tenant_laobank', 'laobank-digital-key-99999', 'ACTIVE', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM consumers WHERE schema_name = 'tenant_laobank');

INSERT INTO consumers (name, schema_name, api_key, status, created_at)
SELECT 'Corporativo Global S.A.', 'tenant_corporativo', 'corp-enterprise-key-88888', 'ACTIVE', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM consumers WHERE schema_name = 'tenant_corporativo');
