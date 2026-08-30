package com.desafio.integrados.autenticacao.service;

import com.desafio.integrados.autenticacao.service.impl.TokenValidationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TokenValidationServiceTest {

    private TokenValidationService tokenValidationService;

    @BeforeEach
    void setUp() {
        tokenValidationService = new TokenValidationServiceImpl(new String[]{"vYQIYxOpyfr==", "outro-token-valido"});
    }

    @Test
    @DisplayName("Deve retornar true para token exato valido")
    void shouldReturnTrueForValidExactToken() {
        assertTrue(tokenValidationService.isValid("vYQIYxOpyfr=="));
    }

    @Test
    @DisplayName("Deve retornar true para token com prefixo Bearer")
    void shouldReturnTrueForValidTokenWithBearerPrefix() {
        assertTrue(tokenValidationService.isValid("Bearer vYQIYxOpyfr=="));
        assertTrue(tokenValidationService.isValid("bearer outro-token-valido"));
    }

    @Test
    @DisplayName("Deve retornar false para token invalido")
    void shouldReturnFalseForInvalidToken() {
        assertFalse(tokenValidationService.isValid("tokenInvalido123"));
        assertFalse(tokenValidationService.isValid("Bearer tokenInvalido123"));
    }

    @Test
    @DisplayName("Deve retornar false para header nulo ou vazio")
    void shouldReturnFalseForNullOrEmptyHeader() {
        assertFalse(tokenValidationService.isValid(null));
        assertFalse(tokenValidationService.isValid(""));
        assertFalse(tokenValidationService.isValid("   "));
        assertFalse(tokenValidationService.isValid("Bearer "));
    }

    @Test
    @DisplayName("Deve extrair o token corretamente ignorando prefixo Bearer e espacos")
    void shouldExtractTokenCorrectly() {
        assertEquals("vYQIYxOpyfr==", tokenValidationService.extractToken("vYQIYxOpyfr=="));
        assertEquals("vYQIYxOpyfr==", tokenValidationService.extractToken("Bearer vYQIYxOpyfr=="));
        assertEquals("vYQIYxOpyfr==", tokenValidationService.extractToken("  Bearer   vYQIYxOpyfr==  "));
        assertNull(tokenValidationService.extractToken(null));
        assertNull(tokenValidationService.extractToken("  "));
        assertNull(tokenValidationService.extractToken("Bearer "));
    }
}
