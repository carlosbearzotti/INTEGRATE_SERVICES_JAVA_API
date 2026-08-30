package com.desafio.integrados.emprestimo.domain.strategy;

import com.desafio.integrados.emprestimo.domain.Customer;
import com.desafio.integrados.emprestimo.domain.Loan;
import com.desafio.integrados.emprestimo.domain.LoanType;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Order(3)
public class ConsignmentLoanStrategy implements LoanEligibilityStrategy {

    private static final BigDecimal INCOME_5000 = new BigDecimal("5000");

    @Override
    public boolean isEligible(Customer customer) {
        return customer.isIncomeGreaterThanOrEqualTo(INCOME_5000);
    }

    @Override
    public Loan getLoan() {
        return new Loan(LoanType.CONSIGNMENT);
    }

    @Override
    public LoanType getLoanType() {
        return LoanType.CONSIGNMENT;
    }
}
