package com.desafio.integrados.senhasegura.domain.rule;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(5)
public class SpecialCharRule implements PasswordRule {

    @Override
    public boolean isValid(String password) {
        if (password == null) {
            return false;
        }
        for (char c : password.toCharArray()) {
            if (!Character.isLetterOrDigit(c)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getFailureMessage() {
        return "A senha deve conter pelo menos um caracter especial (e.g, !@#$%).";
    }

    @Override
    public String getRuleName() {
        return "SPECIAL_CHARACTER";
    }
}
