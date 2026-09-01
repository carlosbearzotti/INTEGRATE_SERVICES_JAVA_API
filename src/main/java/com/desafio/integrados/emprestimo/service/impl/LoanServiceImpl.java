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
    private final com.desafio.integrados.usuario.repository.UserRepository userRepository;
    private final com.desafio.integrados.emprestimo.repository.LoanContractRepository loanContractRepository;

    public LoanServiceImpl(List<LoanEligibilityStrategy> loanStrategies, 
                           com.desafio.integrados.usuario.repository.UserRepository userRepository,
                           com.desafio.integrados.emprestimo.repository.LoanContractRepository loanContractRepository) {
        this.loanStrategies = loanStrategies;
        this.userRepository = userRepository;
        this.loanContractRepository = loanContractRepository;
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

    @Override
    public com.desafio.integrados.emprestimo.dto.ContractLoanResponse contractLoan(Long userId, com.desafio.integrados.emprestimo.dto.ContractLoanRequest request) {
        com.desafio.integrados.usuario.domain.User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        
        // Simulação de regras de negócio de empréstimo (juros, parcelas)
        int installments = 12;
        double rate = 0.05; // 5% a.m. (simplificado)
        double amountWithInterest = request.getAmount() * (1 + (rate * installments));
        double installmentValue = amountWithInterest / installments;
        double iof = request.getAmount() * 0.0038;
        double totalInterest = amountWithInterest - request.getAmount();

        com.desafio.integrados.emprestimo.domain.LoanContract contract = new com.desafio.integrados.emprestimo.domain.LoanContract(
                user, request.getLoanType(), request.getAmount(), amountWithInterest, installments, 
                installmentValue, java.time.LocalDate.now().plusMonths(1), totalInterest, iof, rate
        );

        com.desafio.integrados.emprestimo.domain.LoanContract saved = loanContractRepository.save(contract);

        return new com.desafio.integrados.emprestimo.dto.ContractLoanResponse(
                saved.getId(), saved.getLoanType(), saved.getAmount(), saved.getAmountWithInterest(),
                saved.getInstallments(), saved.getInstallmentValue(), saved.getFirstInstallmentDate(),
                saved.getRate(), saved.getStatus(), saved.getContractedAt()
        );
    }

    @Override
    public List<com.desafio.integrados.emprestimo.dto.ContractLoanResponse> getMyContracts(Long userId) {
        return loanContractRepository.findByUserIdOrderByContractedAtDesc(userId).stream()
                .map(c -> new com.desafio.integrados.emprestimo.dto.ContractLoanResponse(
                        c.getId(), c.getLoanType(), c.getAmount(), c.getAmountWithInterest(),
                        c.getInstallments(), c.getInstallmentValue(), c.getFirstInstallmentDate(),
                        c.getRate(), c.getStatus(), c.getContractedAt()
                ))
                .collect(Collectors.toList());
    }
}
