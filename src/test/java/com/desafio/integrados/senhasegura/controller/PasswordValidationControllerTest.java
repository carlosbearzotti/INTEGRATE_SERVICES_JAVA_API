package com.desafio.integrados.senhasegura.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PasswordValidationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("POST /validate-password - Deve retornar 204 No Content quando a senha for válida (Exemplo do Desafio)")
    void shouldReturn204WhenPasswordIsValid() throws Exception {
        String jsonRequest = """
                {
                    "password": "vYQIYxO&p$yfI^r"
                }
                """;

        mockMvc.perform(post("/validate-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("POST /validate-password - Deve retornar 400 Bad Request com lista de critérios violados quando a senha for inválida")
    void shouldReturn400WhenPasswordIsInvalid() throws Exception {
        String jsonRequest = """
                {
                    "password": "senha"
                }
                """;

        mockMvc.perform(post("/validate-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message", is("A senha informada não atende a todos os critérios de segurança.")))
                .andExpect(jsonPath("$.failures", hasSize(4)))
                .andExpect(jsonPath("$.failures", hasItem("A senha deve possuir pelo menos 08 caracteres.")))
                .andExpect(jsonPath("$.failures", hasItem("A senha deve conter pelo menos uma letra maiúscula.")))
                .andExpect(jsonPath("$.failures", hasItem("A senha deve conter pelo menos um dígito numérico.")))
                .andExpect(jsonPath("$.failures", hasItem("A senha deve conter pelo menos um caracter especial (e.g, !@#$%).")));
    }

    @Test
    @DisplayName("POST /validate-password - Deve retornar 400 Bad Request quando a senha não possui caractere especial")
    void shouldReturn400WhenPasswordMissingSpecialCharacter() throws Exception {
        String jsonRequest = """
                {
                    "password": "SenhaSegura123"
                }
                """;

        mockMvc.perform(post("/validate-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.failures", hasSize(1)))
                .andExpect(jsonPath("$.failures[0]", is("A senha deve conter pelo menos um caracter especial (e.g, !@#$%).")));
    }

    @Test
    @DisplayName("POST /validate-password - Deve retornar 400 Bad Request quando a senha for nula")
    void shouldReturn400WhenPasswordIsNull() throws Exception {
        String jsonRequest = """
                {
                    "password": null
                }
                """;

        mockMvc.perform(post("/validate-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.failures", hasSize(5)));
    }

    @Test
    @DisplayName("POST /validate-password - Deve retornar 400 Bad Request quando o corpo JSON estiver malformado")
    void shouldReturn400WhenJsonIsMalformed() throws Exception {
        String invalidJson = "{ invalid_json_syntax: true ";

        mockMvc.perform(post("/validate-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message", containsString("Formato da mensagem JSON inválido ou malformado.")));
    }
}
