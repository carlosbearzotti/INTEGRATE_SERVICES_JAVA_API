package com.desafio.integrados.senhasegura.domain.rule;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class MinLengthRule implements PasswordRule {

    private static final int MIN_LENGTH = 8;

    @Override
    public boolean isValid(String password) {
        return password != null && password.length() >= MIN_LENGTH;
    }

    @Override
    public String getFailureMessage() {
        return "A senha deve possuir pelo menos 08 caracteres.";
    }

    @Override
    public String getRuleName() {
        return "MIN_LENGTH";
    }
}
