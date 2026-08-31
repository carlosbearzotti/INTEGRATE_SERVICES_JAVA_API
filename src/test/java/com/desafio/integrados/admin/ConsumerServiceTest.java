package com.desafio.integrados.admin;

import com.desafio.integrados.admin.domain.Consumer;
import com.desafio.integrados.admin.dto.ConsumerResponse;
import com.desafio.integrados.admin.dto.CreateConsumerRequest;
import com.desafio.integrados.admin.repository.ConsumerRepository;
import com.desafio.integrados.admin.service.ConsumerService;
import com.desafio.integrados.multitenancy.flyway.FlywayMigrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SuppressWarnings("null")
@ExtendWith(MockitoExtension.class)
class ConsumerServiceTest {

    @Mock
    private ConsumerRepository consumerRepository;

    @Mock
    private FlywayMigrationService flywayMigrationService;

    private ConsumerService consumerService;

    @BeforeEach
    void setUp() {
        consumerService = new ConsumerService(consumerRepository, flywayMigrationService);
    }

    @Test
    void shouldCreateConsumerAndTriggerMigration() {
        CreateConsumerRequest request = new CreateConsumerRequest("Nova Fintech", "novafintech");

        when(consumerRepository.existsBySchemaName("tenant_novafintech")).thenReturn(false);
        when(consumerRepository.save(any(Consumer.class))).thenAnswer(invocation -> {
            Consumer c = invocation.getArgument(0);
            c.setId(10L);
            return c;
        });

        ConsumerResponse response = consumerService.createConsumer(request);

        assertNotNull(response);
        assertEquals("Nova Fintech", response.getName());
        assertEquals("tenant_novafintech", response.getSchemaName());
        assertTrue(response.getApiKey().startsWith("ak_"));
        assertEquals("ACTIVE", response.getStatus());

        verify(flywayMigrationService, times(1)).migrateTenant("tenant_novafintech");
    }

    @Test
    void shouldThrowExceptionWhenSchemaAlreadyExists() {
        CreateConsumerRequest request = new CreateConsumerRequest("Outra Empresa", "empresa_duplicada");

        when(consumerRepository.existsBySchemaName("tenant_empresa_duplicada")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> consumerService.createConsumer(request));
        verify(flywayMigrationService, never()).migrateTenant(anyString());
    }
}
