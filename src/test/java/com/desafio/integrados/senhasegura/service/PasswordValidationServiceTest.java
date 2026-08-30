package com.desafio.integrados.senhasegura.service;

import com.desafio.integrados.senhasegura.domain.rule.*;
import com.desafio.integrados.senhasegura.dto.PasswordValidationRequest;
import com.desafio.integrados.senhasegura.exception.InvalidPasswordException;
import com.desafio.integrados.senhasegura.service.impl.PasswordValidationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PasswordValidationServiceTest {

    private PasswordValidationService service;

    @BeforeEach
    void setUp() {
        List<PasswordRule> rules = List.of(
                new MinLengthRule(),
                new UppercaseRule(),
                new LowercaseRule(),
                new DigitRule(),
                new SpecialCharRule()
        );
        service = new PasswordValidationServiceImpl(rules);
    }

    @Test
    @DisplayName("Deve validar com sucesso quando a senha atender a todos os critérios")
    void shouldValidateSuccessfullyWhenPasswordIsValid() {
        PasswordValidationRequest request = new PasswordValidationRequest("vYQIYxO&p$yfI^r1");

        assertDoesNotThrow(() -> service.validate(request));
    }

    @Test
    @DisplayName("Deve lançar InvalidPasswordException com lista de falhas quando senha for fraca")
    void shouldThrowInvalidPasswordExceptionWhenPasswordIsWeak() {
        PasswordValidationRequest request = new PasswordValidationRequest("senha");

        InvalidPasswordException ex = assertThrows(
                InvalidPasswordException.class,
                () -> service.validate(request)
        );

        assertNotNull(ex.getFailures());
        assertEquals(4, ex.getFailures().size());
        assertTrue(ex.getFailures().contains("A senha deve possuir pelo menos 08 caracteres."));
        assertTrue(ex.getFailures().contains("A senha deve conter pelo menos uma letra maiúscula."));
        assertTrue(ex.getFailures().contains("A senha deve conter pelo menos um dígito numérico."));
        assertTrue(ex.getFailures().contains("A senha deve conter pelo menos um caracter especial (e.g, !@#$%)."));
    }

    @Test
    @DisplayName("Deve lançar exceção informando todas as 5 falhas quando senha for nula")
    void shouldThrowExceptionWithAllFailuresWhenPasswordIsNull() {
        PasswordValidationRequest request = new PasswordValidationRequest(null);

        InvalidPasswordException ex = assertThrows(
                InvalidPasswordException.class,
                () -> service.validate(request)
        );

        assertEquals(5, ex.getFailures().size());
    }
}
