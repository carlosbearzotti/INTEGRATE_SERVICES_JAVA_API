package com.desafio.integrados.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class CreateConsumerRequest {

    @NotBlank(message = "O nome do consumer/empresa é obrigatório")
    private String name;

    @NotBlank(message = "O nome do schema é obrigatório")
    @Pattern(regexp = "^[a-z0-9_]{3,30}$", message = "O schema deve conter apenas letras minúsculas, números e sublinhados (3-30 caracteres)")
    private String schemaName;

    public CreateConsumerRequest() {
    }

    public CreateConsumerRequest(String name, String schemaName) {
        this.name = name;
        this.schemaName = schemaName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }
}
