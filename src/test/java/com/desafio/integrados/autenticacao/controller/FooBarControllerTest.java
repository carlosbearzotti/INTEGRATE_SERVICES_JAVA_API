package com.desafio.integrados.autenticacao.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FooBarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /foo-bar com token exato valido deve retornar 204 No Content")
    void shouldReturn204WhenTokenIsValid() throws Exception {
        mockMvc.perform(get("/foo-bar")
                        .header(HttpHeaders.AUTHORIZATION, "vYQIYxOpyfr=="))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /foo-bar com token Bearer valido deve retornar 204 No Content")
    void shouldReturn204WhenBearerTokenIsValid() throws Exception {
        mockMvc.perform(get("/foo-bar")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer vYQIYxOpyfr=="))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /foo-bar sem header Authorization deve retornar 401 Unauthorized com JSON detalhado")
    void shouldReturn401WhenAuthorizationHeaderIsMissing() throws Exception {
        mockMvc.perform(get("/foo-bar"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status", is(401)))
                .andExpect(jsonPath("$.error", is("Unauthorized")))
                .andExpect(jsonPath("$.message", is("Cabeçalho 'Authorization' ausente ou vazio.")))
                .andExpect(jsonPath("$.path", is("/foo-bar")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    @DisplayName("GET /foo-bar com token invalido deve retornar 401 Unauthorized com JSON detalhado")
    void shouldReturn401WhenTokenIsInvalid() throws Exception {
        mockMvc.perform(get("/foo-bar")
                        .header(HttpHeaders.AUTHORIZATION, "token_invalido_12345"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status", is(401)))
                .andExpect(jsonPath("$.error", is("Unauthorized")))
                .andExpect(jsonPath("$.message", is("Token de autorização inválido ou expirado.")))
                .andExpect(jsonPath("$.path", is("/foo-bar")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    @DisplayName("GET /api/recursos/protegido com token valido deve retornar 200 OK")
    void shouldReturn200ForProtectedEndpointWithValidToken() throws Exception {
        mockMvc.perform(get("/api/recursos/protegido")
                        .header(HttpHeaders.AUTHORIZATION, "vYQIYxOpyfr=="))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("sucesso")));
    }

    @Test
    @DisplayName("GET /api/recursos/protegido sem token deve retornar 401 Unauthorized")
    void shouldReturn401ForProtectedEndpointWithoutToken() throws Exception {
        mockMvc.perform(get("/api/recursos/protegido"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status", is(401)))
                .andExpect(jsonPath("$.error", is("Unauthorized")));
    }

    @Test
    @DisplayName("GET /api/recursos/publico sem token deve retornar 200 OK (bypass por @PublicEndpoint)")
    void shouldReturn200ForPublicEndpointWithoutToken() throws Exception {
        mockMvc.perform(get("/api/recursos/publico"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("sucesso")));
    }
}
