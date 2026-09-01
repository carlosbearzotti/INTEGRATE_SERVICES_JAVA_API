package com.desafio.integrados.emprestimo.service;

import com.desafio.integrados.emprestimo.dto.CustomerLoanRequest;
import com.desafio.integrados.emprestimo.dto.CustomerLoanResponse;

public interface LoanService {

    CustomerLoanResponse determineLoans(CustomerLoanRequest request);
    com.desafio.integrados.emprestimo.dto.ContractLoanResponse contractLoan(Long userId, com.desafio.integrados.emprestimo.dto.ContractLoanRequest request);
    java.util.List<com.desafio.integrados.emprestimo.dto.ContractLoanResponse> getMyContracts(Long userId);
}
