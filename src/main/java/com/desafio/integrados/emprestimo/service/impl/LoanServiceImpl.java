package com.desafio.integrados.emprestimo.service.impl;

import com.desafio.integrados.emprestimo.domain.Customer;
import com.desafio.integrados.emprestimo.domain.strategy.LoanEligibilityStrategy;
import com.desafio.integrados.emprestimo.dto.CustomerLoanRequest;
import com.desafio.integrados.emprestimo.dto.CustomerLoanResponse;
import com.desafio.integrados.emprestimo.dto.LoanResponse;
import com.desafio.integrados.emprestimo.service.LoanService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoanServiceImpl implements LoanService {

    private final List<LoanEligibilityStrategy> loanStrategies;

    public LoanServiceImpl(List<LoanEligibilityStrategy> loanStrategies) {
        this.loanStrategies = loanStrategies;
    }

    @Override
    public CustomerLoanResponse determineLoans(CustomerLoanRequest request) {
        Customer customer = request.toDomain();

        List<LoanResponse> eligibleLoans = loanStrategies.stream()
                .filter(strategy -> strategy.isEligible(customer))
                .map(strategy -> LoanResponse.fromDomain(strategy.getLoan()))
                .collect(Collectors.toList());

        return new CustomerLoanResponse(customer.getName(), eligibleLoans);
    }
}
