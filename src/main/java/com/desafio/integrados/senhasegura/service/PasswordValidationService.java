package com.desafio.integrados.senhasegura.service;

import com.desafio.integrados.senhasegura.dto.PasswordValidationRequest;

public interface PasswordValidationService {

    void validate(PasswordValidationRequest request);
}
