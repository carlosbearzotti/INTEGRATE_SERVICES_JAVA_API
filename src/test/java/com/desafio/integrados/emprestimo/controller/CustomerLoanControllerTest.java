package com.desafio.integrados.emprestimo.controller;

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
class CustomerLoanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("POST /customer-loans - Deve conceder Empréstimo Consignado para cliente com renda alta (7000)")
    void shouldReturnConsignmentLoanForHighIncome() throws Exception {
        String jsonRequest = """
                {
                    "age": 35,
                    "cpf": "275.484.389-23",
                    "name": "Vuxaywua Zukiagou",
                    "income": 7000.00,
                    "location": "SP"
                }
                """;

        mockMvc.perform(post("/customer-loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customer", is("Vuxaywua Zukiagou")))
                .andExpect(jsonPath("$.loans", hasSize(1)))
                .andExpect(jsonPath("$.loans[0].type", is("CONSIGNMENT")))
                .andExpect(jsonPath("$.loans[0].interest_rate", is(2)));
    }

    @Test
    @DisplayName("POST /customer-loans - Deve conceder Pessoal e Garantia para renda <= 3000")
    void shouldReturnPersonalAndGuaranteedForLowIncome() throws Exception {
        String jsonRequest = """
                {
                    "age": 40,
                    "cpf": "123.456.789-00",
                    "name": "João da Silva",
                    "income": 2500.00,
                    "location": "RJ"
                }
                """;

        mockMvc.perform(post("/customer-loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customer", is("João da Silva")))
                .andExpect(jsonPath("$.loans", hasSize(2)))
                .andExpect(jsonPath("$.loans[0].type", is("PERSONAL")))
                .andExpect(jsonPath("$.loans[0].interest_rate", is(4)))
                .andExpect(jsonPath("$.loans[1].type", is("GUARANTEED")))
                .andExpect(jsonPath("$.loans[1].interest_rate", is(3)));
    }

    @Test
    @DisplayName("POST /customer-loans - Deve conceder Pessoal, Garantia e Consignado para renda 5000, < 30 anos e SP")
    void shouldReturnAllThreeLoansForYoungCustomerInSPWithIncome5000() throws Exception {
        String jsonRequest = """
                {
                    "age": 25,
                    "cpf": "987.654.321-99",
                    "name": "Beatriz Oliveira",
                    "income": 5000.00,
                    "location": "SP"
                }
                """;

        mockMvc.perform(post("/customer-loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customer", is("Beatriz Oliveira")))
                .andExpect(jsonPath("$.loans", hasSize(3)))
                .andExpect(jsonPath("$.loans[0].type", is("PERSONAL")))
                .andExpect(jsonPath("$.loans[0].interest_rate", is(4)))
                .andExpect(jsonPath("$.loans[1].type", is("GUARANTEED")))
                .andExpect(jsonPath("$.loans[1].interest_rate", is(3)))
                .andExpect(jsonPath("$.loans[2].type", is("CONSIGNMENT")))
                .andExpect(jsonPath("$.loans[2].interest_rate", is(2)));
    }

    @Test
    @DisplayName("POST /customer-loans - Deve retornar 400 Bad Request se campos obrigatórios estiverem ausentes")
    void shouldReturnBadRequestWhenFieldsAreMissing() throws Exception {
        String jsonRequest = """
                {
                    "name": "",
                    "income": -100.00
                }
                """;

        mockMvc.perform(post("/customer-loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.fieldErrors.name", notNullValue()))
                .andExpect(jsonPath("$.fieldErrors.income", notNullValue()))
                .andExpect(jsonPath("$.fieldErrors.cpf", notNullValue()))
                .andExpect(jsonPath("$.fieldErrors.age", notNullValue()))
                .andExpect(jsonPath("$.fieldErrors.location", notNullValue()));
    }

    @Test
    @DisplayName("POST /customer-loans - Deve retornar 400 Bad Request se o corpo JSON estiver malformado")
    void shouldReturnBadRequestWhenJsonIsMalformed() throws Exception {
        String invalidJson = "{ invalid_json_syntax: true ";

        mockMvc.perform(post("/customer-loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message", containsString("Formato da mensagem JSON inválido")));
    }
}
