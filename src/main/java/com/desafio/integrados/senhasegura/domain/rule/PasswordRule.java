package com.desafio.integrados.senhasegura.domain.rule;

public interface PasswordRule {

    boolean isValid(String password);

    String getFailureMessage();

    String getRuleName();
}
