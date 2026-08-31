package com.desafio.integrados.multitenancy.config;

import com.desafio.integrados.multitenancy.flyway.FlywayMigrationService;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayConfig {

    @Bean
    public FlywayMigrationStrategy flywayMigrationStrategy(FlywayMigrationService flywayMigrationService) {
        return flyway -> {
            // Repair: recalcula checksums locais no schema_history sem apagar dados reais.
            // Necessário quando scripts já aplicados são modificados (ex: desenvolvimento local com H2).
            flyway.repair();
            // 1. Executa migração do schema master (public)
            flyway.migrate();
            // 2. Executa migração dos schemas de todos os tenants registrados
            flywayMigrationService.migrateAllTenants();
        };
    }
}
