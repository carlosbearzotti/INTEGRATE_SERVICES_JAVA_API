package com.desafio.integrados.admin.dto;

import com.desafio.integrados.admin.domain.Consumer;
import java.time.LocalDateTime;

public class ConsumerResponse {

    private Long id;
    private String name;
    private String schemaName;
    private String apiKey;
    private String status;
    private LocalDateTime createdAt;

    public ConsumerResponse() {
    }

    public ConsumerResponse(Consumer consumer) {
        this.id = consumer.getId();
        this.name = consumer.getName();
        this.schemaName = consumer.getSchemaName();
        this.apiKey = consumer.getApiKey();
        this.status = consumer.getStatus();
        this.createdAt = consumer.getCreatedAt();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
