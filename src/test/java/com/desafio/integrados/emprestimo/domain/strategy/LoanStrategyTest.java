package com.desafio.integrados.emprestimo.domain.strategy;

import com.desafio.integrados.emprestimo.domain.Customer;
import com.desafio.integrados.emprestimo.domain.LoanType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class LoanStrategyTest {

    private PersonalLoanStrategy personalLoanStrategy;
    private GuaranteedLoanStrategy guaranteedLoanStrategy;
    private ConsignmentLoanStrategy consignmentLoanStrategy;

    @BeforeEach
    void setUp() {
        personalLoanStrategy = new PersonalLoanStrategy();
        guaranteedLoanStrategy = new GuaranteedLoanStrategy();
        consignmentLoanStrategy = new ConsignmentLoanStrategy();
    }

    @Test
    @DisplayName("Deve retornar LoanType e taxa de juros correta para cada estratégia")
    void shouldReturnCorrectLoanTypeAndInterestRate() {
        assertEquals(LoanType.PERSONAL, personalLoanStrategy.getLoanType());
        assertEquals(4, personalLoanStrategy.getLoan().getInterestRate());

        assertEquals(LoanType.GUARANTEED, guaranteedLoanStrategy.getLoanType());
        assertEquals(3, guaranteedLoanStrategy.getLoan().getInterestRate());

        assertEquals(LoanType.CONSIGNMENT, consignmentLoanStrategy.getLoanType());
        assertEquals(2, consignmentLoanStrategy.getLoan().getInterestRate());
    }

    @ParameterizedTest
    @DisplayName("Elegibilidade Empréstimo Pessoal: Salário <= 3000 deve conceder")
    @CsvSource({
            "0",
            "1500",
            "3000"
    })
    void personalLoan_incomeLessThanOrEqualTo3000_shouldBeEligible(String incomeStr) {
        Customer customer = new Customer("João", "123.456.789-00", 45, new BigDecimal(incomeStr), "RJ");
        assertTrue(personalLoanStrategy.isEligible(customer));
    }

    @Test
    @DisplayName("Elegibilidade Empréstimo Pessoal: Salário entre 3000 e 5000, idade < 30 e SP deve conceder")
    void personalLoan_incomeBetween3000And5000_under30AndSP_shouldBeEligible() {
        Customer customer = new Customer("Maria", "123.456.789-00", 25, new BigDecimal("4000"), "SP");
        assertTrue(personalLoanStrategy.isEligible(customer));
    }

    @Test
    @DisplayName("Elegibilidade Empréstimo Pessoal: Salário entre 3000 e 5000, mas idade >= 30 NÃO deve conceder")
    void personalLoan_incomeBetween3000And5000_age30OrMore_shouldNotBeEligible() {
        Customer customer = new Customer("Maria", "123.456.789-00", 30, new BigDecimal("4000"), "SP");
        assertFalse(personalLoanStrategy.isEligible(customer));
    }

    @Test
    @DisplayName("Elegibilidade Empréstimo Pessoal: Salário entre 3000 e 5000, mas localização diferente de SP NÃO deve conceder")
    void personalLoan_incomeBetween3000And5000_notSP_shouldNotBeEligible() {
        Customer customer = new Customer("Maria", "123.456.789-00", 25, new BigDecimal("4000"), "MG");
        assertFalse(personalLoanStrategy.isEligible(customer));
    }

    @Test
    @DisplayName("Elegibilidade Empréstimo Pessoal: Salário > 5000 NÃO deve conceder")
    void personalLoan_incomeGreaterThan5000_shouldNotBeEligible() {
        Customer customer = new Customer("Carlos", "123.456.789-00", 25, new BigDecimal("5000.01"), "SP");
        assertFalse(personalLoanStrategy.isEligible(customer));
    }

    @ParameterizedTest
    @DisplayName("Elegibilidade Empréstimo com Garantia: Salário <= 3000 deve conceder")
    @CsvSource({
            "0",
            "2000",
            "3000"
    })
    void guaranteedLoan_incomeLessThanOrEqualTo3000_shouldBeEligible(String incomeStr) {
        Customer customer = new Customer("João", "123.456.789-00", 50, new BigDecimal(incomeStr), "BA");
        assertTrue(guaranteedLoanStrategy.isEligible(customer));
    }

    @Test
    @DisplayName("Elegibilidade Empréstimo com Garantia: Salário entre 3000 e 5000, idade < 30 e SP deve conceder")
    void guaranteedLoan_incomeBetween3000And5000_under30AndSP_shouldBeEligible() {
        Customer customer = new Customer("Ana", "123.456.789-00", 29, new BigDecimal("4500"), "SP");
        assertTrue(guaranteedLoanStrategy.isEligible(customer));
    }

    @Test
    @DisplayName("Elegibilidade Empréstimo com Garantia: Salário > 5000 NÃO deve conceder")
    void guaranteedLoan_incomeGreaterThan5000_shouldNotBeEligible() {
        Customer customer = new Customer("Ana", "123.456.789-00", 25, new BigDecimal("7000"), "SP");
        assertFalse(guaranteedLoanStrategy.isEligible(customer));
    }

    @ParameterizedTest
    @DisplayName("Elegibilidade Empréstimo Consignado: Salário >= 5000 deve conceder")
    @CsvSource({
            "5000",
            "5000.01",
            "10000"
    })
    void consignmentLoan_incomeGreaterThanOrEqualTo5000_shouldBeEligible(String incomeStr) {
        Customer customer = new Customer("Pedro", "123.456.789-00", 40, new BigDecimal(incomeStr), "DF");
        assertTrue(consignmentLoanStrategy.isEligible(customer));
    }

    @Test
    @DisplayName("Elegibilidade Empréstimo Consignado: Salário < 5000 NÃO deve conceder")
    void consignmentLoan_incomeLessThan5000_shouldNotBeEligible() {
        Customer customer = new Customer("Pedro", "123.456.789-00", 40, new BigDecimal("4999.99"), "SP");
        assertFalse(consignmentLoanStrategy.isEligible(customer));
    }
}
