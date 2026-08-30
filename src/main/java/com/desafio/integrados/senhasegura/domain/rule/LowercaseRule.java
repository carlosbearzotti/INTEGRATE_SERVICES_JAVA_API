package com.desafio.integrados.senhasegura.domain.rule;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(3)
public class LowercaseRule implements PasswordRule {

    @Override
    public boolean isValid(String password) {
        if (password == null) {
            return false;
        }
        for (char c : password.toCharArray()) {
            if (Character.isLowerCase(c)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getFailureMessage() {
        return "A senha deve conter pelo menos uma letra minúscula.";
    }

    @Override
    public String getRuleName() {
        return "LOWERCASE";
    }
}
