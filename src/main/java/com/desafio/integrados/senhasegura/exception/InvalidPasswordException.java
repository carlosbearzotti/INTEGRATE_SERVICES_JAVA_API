package com.desafio.integrados.senhasegura.exception;

import java.util.List;

public class InvalidPasswordException extends RuntimeException {

    private final List<String> failures;

    public InvalidPasswordException(List<String> failures) {
        super("A senha informada não atende a todos os critérios de segurança.");
        this.failures = failures;
    }

    public List<String> getFailures() {
        return failures;
    }
}
