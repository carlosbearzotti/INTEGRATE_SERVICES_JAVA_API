package com.desafio.integrados.encurtadorurl.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ShortCodeGeneratorTest {

    private ShortCodeGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new ShortCodeGenerator();
    }

    @ParameterizedTest
    @ValueSource(ints = {5, 6, 7, 8, 9, 10})
    @DisplayName("Deve gerar códigos com tamanho correto entre 5 e 10 caracteres")
    void shouldGenerateCodeWithCorrectLength(int length) {
        String code = generator.generateCode(length);

        assertNotNull(code);
        assertEquals(length, code.length());
        assertTrue(generator.isValidCode(code));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 11, 15, -1})
    @DisplayName("Deve lançar exceção para comprimentos inválidos (fora do intervalo 5..10)")
    void shouldThrowExceptionForInvalidLength(int invalidLength) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> generator.generateCode(invalidLength)
        );

        assertTrue(exception.getMessage().contains("entre 5 e 10 caracteres"));
    }

    @Test
    @DisplayName("Deve conter apenas caracteres alfanuméricos")
    void shouldOnlyContainAlphaNumericCharacters() {
        for (int i = 0; i < 100; i++) {
            String code = generator.generateCode(6);
            assertTrue(code.matches("^[a-zA-Z0-9]+$"), "Código deve conter apenas letras e dígitos: " + code);
        }
    }

    @Test
    @DisplayName("Deve gerar códigos com boa dispersão/aleatoriedade sem colisões frequentes")
    void shouldGenerateUniqueCodesStatistically() {
        Set<String> generatedCodes = new HashSet<>();
        int sampleSize = 1000;

        for (int i = 0; i < sampleSize; i++) {
            String code = generator.generateCode(6);
            generatedCodes.add(code);
        }

        // Com 62^6 (56,8 bilhões de combinações), 1000 amostras devem ser 100% únicas
        assertEquals(sampleSize, generatedCodes.size());
    }

    @Test
    @DisplayName("Deve validar corretamente códigos válidos e inválidos")
    void shouldValidateCodesProperly() {
        assertTrue(generator.isValidCode("DXB6V"));
        assertTrue(generator.isValidCode("aB123"));
        assertTrue(generator.isValidCode("1234567890"));

        assertFalse(generator.isValidCode(null));
        assertFalse(generator.isValidCode("abc")); // Menos de 5 caracteres
        assertFalse(generator.isValidCode("12345678901")); // Mais de 10 caracteres
        assertFalse(generator.isValidCode("abc-1")); // Caractere especial inválido
        assertFalse(generator.isValidCode("abc_1")); // Caractere especial inválido
    }
}
