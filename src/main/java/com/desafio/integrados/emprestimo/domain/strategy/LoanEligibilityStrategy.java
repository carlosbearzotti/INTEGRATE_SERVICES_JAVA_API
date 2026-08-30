package com.desafio.integrados.emprestimo.domain.strategy;

import com.desafio.integrados.emprestimo.domain.Customer;
import com.desafio.integrados.emprestimo.domain.Loan;
import com.desafio.integrados.emprestimo.domain.LoanType;

public interface LoanEligibilityStrategy {

    boolean isEligible(Customer customer);

    Loan getLoan();

    LoanType getLoanType();
}
