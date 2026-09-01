package com.desafio.integrados;

import com.desafio.integrados.criptografia.dto.TransactionDTO;
import com.desafio.integrados.encurtadorurl.dto.ShortenUrlRequest;
import com.desafio.integrados.usuario.dto.LoginRequest;
import com.desafio.integrados.usuario.dto.UserRegistrationRequest;
import com.desafio.integrados.usuario.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("null")
@SpringBootTest
@AutoConfigureMockMvc
class EndToEndFlowTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private com.desafio.integrados.criptografia.repository.TransactionRepository transactionRepository;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Fluxo Fintech Completo: Onboarding -> Login -> Empréstimo -> Transação Criptografada -> GPS -> Encurtador de URL")
    void shouldExecuteFullFintechE2EFlow() throws Exception {

        // =========================================================================
        // 1. ONBOARDING (Módulo SenhaSegura)
        // O usuário se cadastra. A API valida a força da senha com regras de segurança.
        // =========================================================================
        UserRegistrationRequest registration = new UserRegistrationRequest(
                "Beatriz Oliveira",
                "beatriz.fintech@exemplo.com",
                "SenhaForte@2026!",
                "987.654.321-99",
                5000.00,
                25,
                20.0,
                10.0
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registration)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.email", is("beatriz.fintech@exemplo.com")));

        // Simula confirmação do código de e-mail (ativação)
        userRepository.findByEmail("beatriz.fintech@exemplo.com").ifPresent(u -> {
            u.setEmailVerified(true);
            userRepository.save(u);
        });

        // =========================================================================
        // 2. LOGIN & AUTENTICAÇÃO (Módulo Autenticação / JWT)
        // O usuário faz login e recebe o Token JWT para autorizar as próximas operações.
        // =========================================================================
        LoginRequest login = new LoginRequest("beatriz.fintech@exemplo.com", "SenhaForte@2026!");

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andReturn();

        JsonNode loginJson = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        String jwtToken = loginJson.get("token").asText();
        String authHeader = "Bearer " + jwtToken;

        // =========================================================================
        // 3. ANÁLISE DE CRÉDITO / PROPOSTAS (Módulo Empréstimo)
        // O usuário autenticado consulta suas opções de crédito com base no seu cadastro.
        // =========================================================================
        mockMvc.perform(get("/api/loans/me")
                        .header(HttpHeaders.AUTHORIZATION, authHeader)
                        .param("location", "SP"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customer", is("Beatriz Oliveira")))
                .andExpect(jsonPath("$.loans", hasSize(3)))
                .andExpect(jsonPath("$.loans[0].type", is("PERSONAL")))
                .andExpect(jsonPath("$.loans[1].type", is("GUARANTEED")))
                .andExpect(jsonPath("$.loans[2].type", is("CONSIGNMENT")));

        // =========================================================================
        // 4. PAGAMENTOS / CARTÕES (Módulo Criptografia AES / RSA)
        // O usuário realiza uma transação com cartão. Os dados sensíveis são criptografados.
        // =========================================================================
        TransactionDTO transactionRequest = new TransactionDTO(
                null,
                null, // userDocument será preenchido automaticamente pelo CPF do usuário autenticado se nulo
                "4111222233334444",
                1500L
        );

        mockMvc.perform(post("/api/transactions")
                        .header(HttpHeaders.AUTHORIZATION, authHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.userId", notNullValue()))
                .andExpect(jsonPath("$.creditCardToken", is("4111222233334444")))
                .andExpect(jsonPath("$.value", is(1500)));

        // =========================================================================
        // 5. AGÊNCIAS E CAIXAS ELETRÔNICOS (Módulo PontoGps)
        // O usuário busca pontos de atendimento / caixas próximos às suas coordenadas.
        // =========================================================================
        mockMvc.perform(get("/pois/nearby")
                        .param("x", "20")
                        .param("y", "10")
                        .param("dmax", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(not(empty()))));

        // =========================================================================
        // 6. INDICAÇÃO / COMPROVANTES (Módulo EncurtadorUrl)
        // O usuário gera um link curto para indicar amigos ou compartilhar comprovante.
        // =========================================================================
        ShortenUrlRequest shortenRequest = new ShortenUrlRequest("https://meubancodigital.com.br/convite/beatriz-oliveira");

        MvcResult shortenResult = mockMvc.perform(post("/shorten-url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shortenRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url", notNullValue()))
                .andReturn();

        JsonNode shortenJson = objectMapper.readTree(shortenResult.getResponse().getContentAsString());
        String shortUrl = shortenJson.get("url").asText();
        String shortCode = shortUrl.substring(shortUrl.lastIndexOf("/") + 1);

        // Acessa o link encurtado e valida o redirecionamento (HTTP 302 Found)
        mockMvc.perform(get("/" + shortCode))
                .andExpect(status().isFound());
    }
}
