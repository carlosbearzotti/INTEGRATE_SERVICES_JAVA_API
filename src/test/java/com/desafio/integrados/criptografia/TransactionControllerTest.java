package com.desafio.integrados.criptografia;

import com.desafio.integrados.criptografia.dto.TransactionDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.security.test.context.support.WithMockUser;

@SuppressWarnings("null")
@SpringBootTest
@AutoConfigureMockMvc
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/transactions - Deve criar transação criptografada e retornar dados legíveis")
    @WithMockUser(username = "teste@exemplo.com")
    void shouldCreateTransaction() throws Exception {
        TransactionDTO request = new TransactionDTO(null, "12345678900", "4111111111111111", 5000L);

        mockMvc.perform(post("/api/transactions")
                        .requestAttr("authenticatedUserId", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.userDocument", is("12345678900")))
                .andExpect(jsonPath("$.creditCardToken", is("4111111111111111")))
                .andExpect(jsonPath("$.value", is(5000)));
    }
}
