package com.desafio.integrados.emprestimo.service;

import com.desafio.integrados.emprestimo.dto.CustomerLoanRequest;
import com.desafio.integrados.emprestimo.dto.CustomerLoanResponse;

public interface LoanService {

    CustomerLoanResponse determineLoans(CustomerLoanRequest request);
}
