package com.desafio.integrados.usuario.controller;

import com.desafio.integrados.usuario.dto.LoginRequest;
import com.desafio.integrados.usuario.dto.UserRegistrationRequest;
import com.desafio.integrados.usuario.repository.UserRepository;
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
class AuthControllerTest {

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
    @DisplayName("POST /api/auth/register - Deve cadastrar usuário com sucesso quando a senha for forte")
    void shouldRegisterUserSuccessfullyWithStrongPassword() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest(
                "Carlos Silva",
                "carlos@exemplo.com",
                "SenhaForte@123",
                "123.456.789-00",
                5000.0,
                28,
                -23.5505,
                -46.6333
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is("Carlos Silva")))
                .andExpect(jsonPath("$.email", is("carlos@exemplo.com")))
                .andExpect(jsonPath("$.cpf", is("123.456.789-00")))
                .andExpect(jsonPath("$.income", is(5000.0)))
                .andExpect(jsonPath("$.age", is(28)));
    }

    @Test
    @DisplayName("POST /api/auth/register - Deve rejeitar cadastro quando a senha for fraca (SenhaSegura)")
    void shouldRejectRegistrationWhenPasswordIsWeak() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest(
                "Carlos Silva",
                "carlos@exemplo.com",
                "fraca",
                "123.456.789-00",
                5000.0,
                28,
                -23.5505,
                -46.6333
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.failures", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @DisplayName("POST /api/auth/login - Deve autenticar e retornar JWT quando as credenciais forem válidas")
    void shouldLoginSuccessfullyAndReturnJwt() throws Exception {
        // Registra o usuário primeiro
        UserRegistrationRequest registerReq = new UserRegistrationRequest(
                "Ana Souza",
                "ana@exemplo.com",
                "Segura@2026!",
                "987.654.321-11",
                4500.0,
                32,
                -22.9068,
                -43.1729
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerReq)))
                .andExpect(status().isCreated());

        // Realiza o login
        LoginRequest loginReq = new LoginRequest("ana@exemplo.com", "Segura@2026!");

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.type", is("Bearer")))
                .andExpect(jsonPath("$.name", is("Ana Souza")))
                .andExpect(jsonPath("$.email", is("ana@exemplo.com")))
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        String token = objectMapper.readTree(responseJson).get("token").asText();

        // Consulta o perfil com o token gerado
        mockMvc.perform(get("/api/auth/me")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Ana Souza")))
                .andExpect(jsonPath("$.email", is("ana@exemplo.com")))
                .andExpect(jsonPath("$.cpf", is("987.654.321-11")));
    }

    @Test
    @DisplayName("POST /api/auth/login - Deve retornar 401 quando senha for incorreta")
    void shouldReturn401WhenPasswordIsIncorrect() throws Exception {
        UserRegistrationRequest registerReq = new UserRegistrationRequest(
                "Lucas Lima",
                "lucas@exemplo.com",
                "Forte@12345",
                "111.222.333-44",
                3000.0,
                25,
                null,
                null
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerReq)))
                .andExpect(status().isCreated());

        LoginRequest loginReq = new LoginRequest("lucas@exemplo.com", "SenhaErrada!1");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isUnauthorized());
    }
}
