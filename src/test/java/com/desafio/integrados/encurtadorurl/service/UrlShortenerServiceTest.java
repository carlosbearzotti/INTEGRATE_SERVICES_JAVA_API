package com.desafio.integrados.encurtadorurl.service;

import com.desafio.integrados.encurtadorurl.config.UrlShortenerProperties;
import com.desafio.integrados.encurtadorurl.domain.UrlMapping;
import com.desafio.integrados.encurtadorurl.exception.InvalidUrlException;
import com.desafio.integrados.encurtadorurl.exception.UrlNotFoundException;
import com.desafio.integrados.encurtadorurl.repository.UrlMappingRepository;
import com.desafio.integrados.encurtadorurl.service.impl.UrlShortenerServiceImpl;
import com.desafio.integrados.encurtadorurl.util.ShortCodeGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("null")
@ExtendWith(MockitoExtension.class)
class UrlShortenerServiceTest {

    @Mock
    private UrlMappingRepository repository;

    @Mock
    private ShortCodeGenerator codeGenerator;

    @Mock
    private UrlShortenerProperties properties;

    @InjectMocks
    private UrlShortenerServiceImpl service;

    @BeforeEach
    void setUp() {
        lenient().when(properties.getCodeLength()).thenReturn(6);
        lenient().when(properties.getExpirationDays()).thenReturn(30L);
    }

    @Test
    @DisplayName("Deve encurtar uma URL válida com sucesso e persistir com prazo de validade")
    void shouldShortenValidUrlSuccessfully() {
        String originalUrl = "https://backendbrasil.com.br";
        String requestBaseUrl = "http://localhost:8080";
        String generatedCode = "DXB6V";

        when(codeGenerator.generateCode(6)).thenReturn(generatedCode);
        when(repository.existsByShortCode(generatedCode)).thenReturn(false);

        String result = service.shortenUrl(originalUrl, requestBaseUrl);

        assertEquals("http://localhost:8080/DXB6V", result);

        ArgumentCaptor<UrlMapping> captor = ArgumentCaptor.forClass(UrlMapping.class);
        verify(repository).save(captor.capture());

        UrlMapping savedMapping = captor.getValue();
        assertEquals(generatedCode, savedMapping.getShortCode());
        assertEquals(originalUrl, savedMapping.getOriginalUrl());
        assertNotNull(savedMapping.getCreatedAt());
        assertNotNull(savedMapping.getExpiresAt());
        assertTrue(savedMapping.getExpiresAt().isAfter(savedMapping.getCreatedAt()));
    }

    @Test
    @DisplayName("Deve usar base URL configurada caso esteja presente nas propriedades")
    void shouldUseConfiguredBaseUrlWhenPresent() {
        String originalUrl = "https://backendbrasil.com.br";
        String generatedCode = "DXB6V";

        when(properties.getBaseUrl()).thenReturn("https://xxx.com");
        when(codeGenerator.generateCode(6)).thenReturn(generatedCode);
        when(repository.existsByShortCode(generatedCode)).thenReturn(false);

        String result = service.shortenUrl(originalUrl, "http://localhost:8080");

        assertEquals("https://xxx.com/DXB6V", result);
    }

    @Test
    @DisplayName("Deve tentar gerar outro código em caso de colisão")
    void shouldRetryGenerationOnCollision() {
        String originalUrl = "https://backendbrasil.com.br";
        when(codeGenerator.generateCode(6)).thenReturn("COLLIDE", "UNIQUE1");
        when(repository.existsByShortCode("COLLIDE")).thenReturn(true);
        when(repository.existsByShortCode("UNIQUE1")).thenReturn(false);

        String result = service.shortenUrl(originalUrl, "http://localhost:8080");

        assertEquals("http://localhost:8080/UNIQUE1", result);
        verify(codeGenerator, times(2)).generateCode(6);
    }

    @Test
    @DisplayName("Deve lançar InvalidUrlException para URLs inválidas ou sem protocolo")
    void shouldThrowInvalidUrlExceptionForInvalidUrls() {
        assertThrows(InvalidUrlException.class, () -> service.shortenUrl("", "http://localhost:8080"));
        assertThrows(InvalidUrlException.class, () -> service.shortenUrl(null, "http://localhost:8080"));
        assertThrows(InvalidUrlException.class, () -> service.shortenUrl("backendbrasil.com.br", "http://localhost:8080"));
        assertThrows(InvalidUrlException.class, () -> service.shortenUrl("ftp://backendbrasil.com.br", "http://localhost:8080"));
    }

    @Test
    @DisplayName("Deve recuperar URL original e incrementar contador de acessos")
    void shouldGetOriginalUrlAndIncrementAccessCount() {
        String shortCode = "DXB6V";
        String originalUrl = "https://backendbrasil.com.br";
        LocalDateTime now = LocalDateTime.now();
        UrlMapping mapping = new UrlMapping(shortCode, originalUrl, now.minusDays(1), now.plusDays(29));

        when(repository.findByShortCode(shortCode)).thenReturn(Optional.of(mapping));

        String result = service.getOriginalUrlAndTrackAccess(shortCode);

        assertEquals(originalUrl, result);
        assertEquals(1, mapping.getAccessCount());
        verify(repository).save(mapping);
    }

    @Test
    @DisplayName("Deve lançar UrlNotFoundException quando o código não existir no banco")
    void shouldThrowUrlNotFoundExceptionWhenCodeDoesNotExist() {
        when(repository.findByShortCode("NOTFOUND")).thenReturn(Optional.empty());

        assertThrows(UrlNotFoundException.class, () -> service.getOriginalUrlAndTrackAccess("NOTFOUND"));
    }

    @Test
    @DisplayName("Deve lançar UrlNotFoundException quando o link encurtado estiver expirado")
    void shouldThrowUrlNotFoundExceptionWhenUrlIsExpired() {
        String shortCode = "EXPIRED";
        LocalDateTime now = LocalDateTime.now();
        UrlMapping mapping = new UrlMapping(shortCode, "https://example.com", now.minusDays(40), now.minusDays(10));

        when(repository.findByShortCode(shortCode)).thenReturn(Optional.of(mapping));

        UrlNotFoundException ex = assertThrows(UrlNotFoundException.class, () -> service.getOriginalUrlAndTrackAccess(shortCode));
        assertTrue(ex.getMessage().contains("expirou"));
    }
}
