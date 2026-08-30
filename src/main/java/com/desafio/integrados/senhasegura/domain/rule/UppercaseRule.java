package com.desafio.integrados.senhasegura.domain.rule;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class UppercaseRule implements PasswordRule {

    @Override
    public boolean isValid(String password) {
        if (password == null) {
            return false;
        }
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getFailureMessage() {
        return "A senha deve conter pelo menos uma letra maiúscula.";
    }

    @Override
    public String getRuleName() {
        return "UPPERCASE";
    }
}
