package com.desafio.integrados.admin.service;

import com.desafio.integrados.admin.domain.Consumer;
import com.desafio.integrados.admin.dto.ConsumerResponse;
import com.desafio.integrados.admin.dto.CreateConsumerRequest;
import com.desafio.integrados.admin.repository.ConsumerRepository;
import com.desafio.integrados.multitenancy.flyway.FlywayMigrationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ConsumerService {

    private final ConsumerRepository consumerRepository;
    private final FlywayMigrationService flywayMigrationService;

    public ConsumerService(ConsumerRepository consumerRepository, FlywayMigrationService flywayMigrationService) {
        this.consumerRepository = consumerRepository;
        this.flywayMigrationService = flywayMigrationService;
    }

    @Transactional
    public ConsumerResponse createConsumer(CreateConsumerRequest request) {
        String schemaName = request.getSchemaName().toLowerCase();
        if (!schemaName.startsWith("tenant_")) {
            schemaName = "tenant_" + schemaName;
        }

        if (consumerRepository.existsBySchemaName(schemaName)) {
            throw new IllegalArgumentException("Já existe um consumer cadastrado com o schema '" + schemaName + "'");
        }

        String generatedApiKey = "ak_" + UUID.randomUUID().toString().replace("-", "");

        Consumer consumer = new Consumer(request.getName(), schemaName, generatedApiKey);
        Consumer saved = consumerRepository.save(consumer);

        // Provisiona o novo Schema e executa as migrações isoladas do Flyway
        flywayMigrationService.migrateTenant(schemaName);

        return new ConsumerResponse(saved);
    }

    public List<ConsumerResponse> findAllConsumers() {
        return consumerRepository.findAll()
                .stream()
                .map(ConsumerResponse::new)
                .collect(Collectors.toList());
    }
}
