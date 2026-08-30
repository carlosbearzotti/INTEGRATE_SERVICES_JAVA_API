package com.desafio.integrados.senhasegura.domain.rule;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordRuleTest {

    @Test
    @DisplayName("MinLengthRule deve validar corretamente tamanho mínimo de 8 caracteres")
    void testMinLengthRule() {
        MinLengthRule rule = new MinLengthRule();

        assertTrue(rule.isValid("12345678"));
        assertTrue(rule.isValid("123456789"));
        assertFalse(rule.isValid("1234567"));
        assertFalse(rule.isValid(""));
        assertFalse(rule.isValid(null));
        assertEquals("MIN_LENGTH", rule.getRuleName());
        assertEquals("A senha deve possuir pelo menos 08 caracteres.", rule.getFailureMessage());
    }

    @Test
    @DisplayName("UppercaseRule deve validar se contém pelo menos uma letra maiúscula")
    void testUppercaseRule() {
        UppercaseRule rule = new UppercaseRule();

        assertTrue(rule.isValid("aBcd"));
        assertTrue(rule.isValid("ABCD"));
        assertFalse(rule.isValid("abcd"));
        assertFalse(rule.isValid("123456!@#"));
        assertFalse(rule.isValid(""));
        assertFalse(rule.isValid(null));
        assertEquals("UPPERCASE", rule.getRuleName());
        assertEquals("A senha deve conter pelo menos uma letra maiúscula.", rule.getFailureMessage());
    }

    @Test
    @DisplayName("LowercaseRule deve validar se contém pelo menos uma letra minúscula")
    void testLowercaseRule() {
        LowercaseRule rule = new LowercaseRule();

        assertTrue(rule.isValid("Abcd"));
        assertTrue(rule.isValid("abcd"));
        assertFalse(rule.isValid("ABCD"));
        assertFalse(rule.isValid("123456!@#"));
        assertFalse(rule.isValid(""));
        assertFalse(rule.isValid(null));
        assertEquals("LOWERCASE", rule.getRuleName());
        assertEquals("A senha deve conter pelo menos uma letra minúscula.", rule.getFailureMessage());
    }

    @Test
    @DisplayName("DigitRule deve validar se contém pelo menos um dígito numérico")
    void testDigitRule() {
        DigitRule rule = new DigitRule();

        assertTrue(rule.isValid("abc1def"));
        assertTrue(rule.isValid("9999"));
        assertFalse(rule.isValid("abcdef"));
        assertFalse(rule.isValid("!@#$%^"));
        assertFalse(rule.isValid(""));
        assertFalse(rule.isValid(null));
        assertEquals("DIGIT", rule.getRuleName());
        assertEquals("A senha deve conter pelo menos um dígito numérico.", rule.getFailureMessage());
    }

    @Test
    @DisplayName("SpecialCharRule deve validar se contém pelo menos um caractere especial")
    void testSpecialCharRule() {
        SpecialCharRule rule = new SpecialCharRule();

        assertTrue(rule.isValid("abc!def"));
        assertTrue(rule.isValid("Senha@123"));
        assertTrue(rule.isValid("vYQIYxO&p$yfI^r"));
        assertTrue(rule.isValid("#$%"));
        assertFalse(rule.isValid("SenhaSegura123"));
        assertFalse(rule.isValid(""));
        assertFalse(rule.isValid(null));
        assertEquals("SPECIAL_CHARACTER", rule.getRuleName());
        assertEquals("A senha deve conter pelo menos um caracter especial (e.g, !@#$%).", rule.getFailureMessage());
    }
}
