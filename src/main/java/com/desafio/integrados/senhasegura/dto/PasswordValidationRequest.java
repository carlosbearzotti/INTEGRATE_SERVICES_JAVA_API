package com.desafio.integrados.senhasegura.dto;

import com.desafio.integrados.senhasegura.domain.Password;

public class PasswordValidationRequest {

    private String password;

    public PasswordValidationRequest() {
    }

    public PasswordValidationRequest(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Password toDomain() {
        return new Password(password);
    }
}
