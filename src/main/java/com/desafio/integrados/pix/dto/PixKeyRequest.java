package com.desafio.integrados.pix.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class PixKeyRequest {

    @NotBlank(message = "O valor da chave não pode estar vazio")
    private String keyValue;

    @NotBlank(message = "O tipo da chave não pode estar vazio")
    @Pattern(regexp = "^(CPF|EMAIL|PHONE|RANDOM)$", message = "O tipo deve ser CPF, EMAIL, PHONE ou RANDOM")
    private String keyType;

    public PixKeyRequest() {}

    public PixKeyRequest(String keyValue, String keyType) {
        this.keyValue = keyValue;
        this.keyType = keyType;
    }

    public String getKeyValue() { return keyValue; }
    public void setKeyValue(String keyValue) { this.keyValue = keyValue; }
    public String getKeyType() { return keyType; }
    public void setKeyType(String keyType) { this.keyType = keyType; }
}
