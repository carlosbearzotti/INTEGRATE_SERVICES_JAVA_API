package com.desafio.integrados.multitenancy.flyway;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Service
public class FlywayMigrationService {

    private static final Logger log = LoggerFactory.getLogger(FlywayMigrationService.class);
    private final DataSource dataSource;

    public FlywayMigrationService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void migrateAllTenants() {
        log.info("Iniciando verificação e migração dos schemas de todos os tenants ativos...");
        List<String> schemas = getActiveTenantSchemas();

        // Sempre garante que o tenant padrão 'tenant_fintech' exista
        if (!schemas.contains("tenant_fintech")) {
            schemas.add("tenant_fintech");
        }

        for (String schema : schemas) {
            try {
                migrateTenant(schema);
            } catch (Exception e) {
                log.error("Falha ao migrar schema do tenant '{}': {}", schema, e.getMessage(), e);
            }
        }
        log.info("Migração de todos os tenants concluída com sucesso.");
    }

    public void migrateTenant(String schemaName) {
        log.info("Executando Flyway Migration para o schema '{}'...", schemaName);

        // Garante que o schema existe no PostgreSQL
        createSchemaIfNotExists(schemaName);

        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration/tenant")
                .schemas(schemaName)
                .defaultSchema(schemaName)
                .baselineOnMigrate(true)
                .load();

        flyway.migrate();
        log.info("Schema '{}' migrado com sucesso!", schemaName);
    }

    private void createSchemaIfNotExists(String schemaName) {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE SCHEMA IF NOT EXISTS \"" + schemaName + "\"");
        } catch (SQLException e) {
            log.error("Erro ao criar schema '{}': {}", schemaName, e.getMessage());
            throw new RuntimeException("Não foi possível criar o schema do tenant: " + schemaName, e);
        }
    }

    private List<String> getActiveTenantSchemas() {
        List<String> schemas = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            // Consulta os tenants cadastrados no schema public
            try (ResultSet rs = statement.executeQuery("SELECT schema_name FROM public.consumers WHERE status = 'ACTIVE'")) {
                while (rs.next()) {
                    schemas.add(rs.getString("schema_name"));
                }
            }
        } catch (SQLException e) {
            log.warn("Tabela public.consumers não encontrada ou vazia durante inicialização: {}", e.getMessage());
        }
        return schemas;
    }
}
