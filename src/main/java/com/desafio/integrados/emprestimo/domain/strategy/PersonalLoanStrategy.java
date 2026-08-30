package com.desafio.integrados.emprestimo.domain.strategy;

import com.desafio.integrados.emprestimo.domain.Customer;
import com.desafio.integrados.emprestimo.domain.Loan;
import com.desafio.integrados.emprestimo.domain.LoanType;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Order(1)
public class PersonalLoanStrategy implements LoanEligibilityStrategy {

    private static final BigDecimal INCOME_3000 = new BigDecimal("3000");
    private static final BigDecimal INCOME_5000 = new BigDecimal("5000");
    private static final String LOCATION_SP = "SP";
    private static final int MAX_AGE_EXCLUSIVE = 30;

    @Override
    public boolean isEligible(Customer customer) {
        if (customer.isIncomeLessThanOrEqualTo(INCOME_3000)) {
            return true;
        }

        return customer.isIncomeBetween(INCOME_3000, INCOME_5000)
                && customer.isAgeLessThan(MAX_AGE_EXCLUSIVE)
                && customer.isLocation(LOCATION_SP);
    }

    @Override
    public Loan getLoan() {
        return new Loan(LoanType.PERSONAL);
    }

    @Override
    public LoanType getLoanType() {
        return LoanType.PERSONAL;
    }
}
