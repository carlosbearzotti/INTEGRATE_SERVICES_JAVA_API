package com.desafio.integrados.usuario.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class VerifyAccountRequest {

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "Formato de e-mail inválido.")
    private String email;

    @NotBlank(message = "O código de verificação é obrigatório.")
    @Size(min = 6, max = 6, message = "O código de verificação deve ter exatamente 6 dígitos.")
    private String code;

    public VerifyAccountRequest() {
    }

    public VerifyAccountRequest(String email, String code) {
        this.email = email;
        this.code = code;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
}
