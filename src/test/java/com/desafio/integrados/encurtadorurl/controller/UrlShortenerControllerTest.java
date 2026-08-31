package com.desafio.integrados.encurtadorurl.controller;

import com.desafio.integrados.encurtadorurl.exception.UrlNotFoundException;
import com.desafio.integrados.encurtadorurl.service.UrlShortenerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SuppressWarnings("null")
@WebMvcTest(UrlShortenerController.class)
@Import(com.desafio.integrados.autenticacao.config.SecurityConfig.class)
class UrlShortenerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UrlShortenerService urlShortenerService;

    @Test
    @DisplayName("POST /shorten-url - Deve retornar 200 OK com a URL encurtada quando o payload for válido")
    void shouldReturn200AndShortenedUrlWhenRequestIsValid() throws Exception {
        String originalUrl = "https://backendbrasil.com.br";
        String shortenedUrl = "http://localhost:8080/DXB6V";

        when(urlShortenerService.shortenUrl(eq(originalUrl), anyString())).thenReturn(shortenedUrl);

        String requestJson = """
                {
                    "url": "https://backendbrasil.com.br"
                }
                """;

        mockMvc.perform(post("/shorten-url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.url").value(shortenedUrl));
    }

    @Test
    @DisplayName("POST /shorten-url - Deve retornar 400 Bad Request quando a URL for vazia ou inválida")
    void shouldReturn400BadRequestWhenUrlIsInvalid() throws Exception {
        String invalidRequestJson = """
                {
                    "url": "not-a-valid-url"
                }
                """;

        mockMvc.perform(post("/shorten-url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    @Test
    @DisplayName("GET /{shortCode} - Deve retornar 302 Found com header Location para URL original")
    void shouldReturn302FoundAndRedirectWhenShortCodeExists() throws Exception {
        String shortCode = "DXB6V";
        String originalUrl = "https://backendbrasil.com.br";

        when(urlShortenerService.getOriginalUrlAndTrackAccess(shortCode)).thenReturn(originalUrl);

        mockMvc.perform(get("/" + shortCode))
                .andExpect(status().isFound())
                .andExpect(header().string(HttpHeaders.LOCATION, originalUrl));
    }

    @Test
    @DisplayName("GET /{shortCode} - Deve retornar 404 Not Found quando o código não existir ou estiver expirado")
    void shouldReturn404NotFoundWhenShortCodeDoesNotExist() throws Exception {
        String shortCode = "NOTFD";

        when(urlShortenerService.getOriginalUrlAndTrackAccess(shortCode))
                .thenThrow(new UrlNotFoundException("URL encurtada não encontrada para o código: " + shortCode));

        mockMvc.perform(get("/" + shortCode))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("URL encurtada não encontrada para o código: " + shortCode));
    }
}
