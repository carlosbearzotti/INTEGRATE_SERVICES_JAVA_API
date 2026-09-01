package com.desafio.integrados.emprestimo.service;

import com.desafio.integrados.emprestimo.domain.LoanType;
import com.desafio.integrados.emprestimo.domain.strategy.ConsignmentLoanStrategy;
import com.desafio.integrados.emprestimo.domain.strategy.GuaranteedLoanStrategy;
import com.desafio.integrados.emprestimo.domain.strategy.PersonalLoanStrategy;
import com.desafio.integrados.emprestimo.dto.CustomerLoanRequest;
import com.desafio.integrados.emprestimo.dto.CustomerLoanResponse;
import com.desafio.integrados.emprestimo.service.impl.LoanServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class LoanServiceTest {

    private LoanService loanService;
    private com.desafio.integrados.usuario.repository.UserRepository userRepository;
    private com.desafio.integrados.emprestimo.repository.LoanContractRepository loanContractRepository;

    @BeforeEach
    void setUp() {
        userRepository = mock(com.desafio.integrados.usuario.repository.UserRepository.class);
        loanContractRepository = mock(com.desafio.integrados.emprestimo.repository.LoanContractRepository.class);
        loanService = new LoanServiceImpl(List.of(
                new PersonalLoanStrategy(),
                new GuaranteedLoanStrategy(),
                new ConsignmentLoanStrategy()
        ), userRepository, loanContractRepository);
    }

    @Test
    @DisplayName("Cliente com renda <= 3000 deve ter acesso a Empréstimo Pessoal e com Garantia")
    void shouldReturnPersonalAndGuaranteedForIncomeUnder3000() {
        CustomerLoanRequest request = new CustomerLoanRequest(
                35,
                "123.456.789-10",
                "Erick Rocha",
                new BigDecimal("2500.00"),
                "MG"
        );

        CustomerLoanResponse response = loanService.determineLoans(request);

        assertNotNull(response);
        assertEquals("Erick Rocha", response.getCustomer());
        assertEquals(2, response.getLoans().size());
        assertEquals(LoanType.PERSONAL, response.getLoans().get(0).getType());
        assertEquals(4, response.getLoans().get(0).getInterestRate());
        assertEquals(LoanType.GUARANTEED, response.getLoans().get(1).getType());
        assertEquals(3, response.getLoans().get(1).getInterestRate());
    }

    @Test
    @DisplayName("Cliente com renda entre 3000 e 5000, menos de 30 anos e de SP deve ter acesso a Pessoal e Garantia")
    void shouldReturnPersonalAndGuaranteedForYoungCustomerInSPWithMediumIncome() {
        CustomerLoanRequest request = new CustomerLoanRequest(
                26,
                "123.456.789-10",
                "Julia Silva",
                new BigDecimal("3500.00"),
                "SP"
        );

        CustomerLoanResponse response = loanService.determineLoans(request);

        assertNotNull(response);
        assertEquals("Julia Silva", response.getCustomer());
        assertEquals(2, response.getLoans().size());
        assertEquals(LoanType.PERSONAL, response.getLoans().get(0).getType());
        assertEquals(LoanType.GUARANTEED, response.getLoans().get(1).getType());
    }

    @Test
    @DisplayName("Cliente com renda entre 3000 e 5000, mas de fora de SP não deve ter empréstimos")
    void shouldReturnNoLoansForMediumIncomeOutsideSP() {
        CustomerLoanRequest request = new CustomerLoanRequest(
                26,
                "123.456.789-10",
                "Lucas Lima",
                new BigDecimal("3500.00"),
                "RJ"
        );

        CustomerLoanResponse response = loanService.determineLoans(request);

        assertNotNull(response);
        assertEquals("Lucas Lima", response.getCustomer());
        assertTrue(response.getLoans().isEmpty());
    }

    @Test
    @DisplayName("Cliente com renda >= 5000 (ex: 7000) e mais de 30 anos ou fora de SP deve ter apenas Consignado")
    void shouldReturnConsignmentForHighIncome() {
        CustomerLoanRequest request = new CustomerLoanRequest(
                35,
                "275.484.389-23",
                "Vuxaywua Zukiagou",
                new BigDecimal("7000.00"),
                "SP"
        );

        CustomerLoanResponse response = loanService.determineLoans(request);

        assertNotNull(response);
        assertEquals("Vuxaywua Zukiagou", response.getCustomer());
        assertEquals(1, response.getLoans().size());
        assertEquals(LoanType.CONSIGNMENT, response.getLoans().get(0).getType());
        assertEquals(2, response.getLoans().get(0).getInterestRate());
    }

    @Test
    @DisplayName("Cliente com renda exatamente 5000, menos de 30 anos e de SP deve ter Pessoal, Garantia e Consignado")
    void shouldReturnAllThreeLoansForBoundaryIncome5000YoungInSP() {
        CustomerLoanRequest request = new CustomerLoanRequest(
                28,
                "111.222.333-44",
                "Camila Souza",
                new BigDecimal("5000.00"),
                "SP"
        );

        CustomerLoanResponse response = loanService.determineLoans(request);

        assertNotNull(response);
        assertEquals("Camila Souza", response.getCustomer());
        assertEquals(3, response.getLoans().size());
        assertEquals(LoanType.PERSONAL, response.getLoans().get(0).getType());
        assertEquals(LoanType.GUARANTEED, response.getLoans().get(1).getType());
        assertEquals(LoanType.CONSIGNMENT, response.getLoans().get(2).getType());
    }
}
