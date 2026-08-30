package com.desafio.integrados.emprestimo.controller;

import com.desafio.integrados.autenticacao.annotation.PublicEndpoint;
import com.desafio.integrados.emprestimo.dto.CustomerLoanRequest;
import com.desafio.integrados.emprestimo.dto.CustomerLoanResponse;
import com.desafio.integrados.emprestimo.service.LoanService;
import com.desafio.integrados.usuario.domain.User;
import com.desafio.integrados.usuario.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
public class CustomerLoanController {

    private final LoanService loanService;
    private final UserRepository userRepository;

    public CustomerLoanController(LoanService loanService, UserRepository userRepository) {
        this.loanService = loanService;
        this.userRepository = userRepository;
    }

    @PostMapping("/customer-loans")
    @PublicEndpoint
    public ResponseEntity<CustomerLoanResponse> determineCustomerLoans(@Valid @RequestBody CustomerLoanRequest request) {
        CustomerLoanResponse response = loanService.determineLoans(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping({"/api/loans/me", "/customer-loans/me"})
    public ResponseEntity<CustomerLoanResponse> getLoansForAuthenticatedUser(
            @RequestParam(required = false, defaultValue = "SP") String location,
            HttpServletRequest request
    ) {
        Long userId = (Long) request.getAttribute("authenticatedUserId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        CustomerLoanRequest loanRequest = new CustomerLoanRequest(
                user.getAge(),
                user.getCpf(),
                user.getName(),
                BigDecimal.valueOf(user.getIncome()),
                location
        );

        CustomerLoanResponse response = loanService.determineLoans(loanRequest);
        return ResponseEntity.ok(response);
    }
}
