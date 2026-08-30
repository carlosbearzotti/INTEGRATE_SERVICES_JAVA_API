package com.desafio.integrados.senhasegura.service.impl;

import com.desafio.integrados.senhasegura.domain.rule.PasswordRule;
import com.desafio.integrados.senhasegura.dto.PasswordValidationRequest;
import com.desafio.integrados.senhasegura.exception.InvalidPasswordException;
import com.desafio.integrados.senhasegura.service.PasswordValidationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PasswordValidationServiceImpl implements PasswordValidationService {

    private final List<PasswordRule> rules;

    public PasswordValidationServiceImpl(List<PasswordRule> rules) {
        this.rules = rules;
    }

    @Override
    public void validate(PasswordValidationRequest request) {
        String password = (request != null) ? request.getPassword() : null;

        List<String> failures = rules.stream()
                .filter(rule -> !rule.isValid(password))
                .map(rule -> rule.getFailureMessage())
                .collect(Collectors.toList());

        if (!failures.isEmpty()) {
            throw new InvalidPasswordException(failures);
        }
    }
}
