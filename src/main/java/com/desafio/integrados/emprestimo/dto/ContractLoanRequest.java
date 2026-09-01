package com.desafio.integrados.emprestimo.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ContractLoanRequest {

    @NotBlank(message = "O tipo de empréstimo não pode estar vazio")
    private String loanType; // PERSONAL, GUARANTEED, CONSIGNMENT

    @NotNull(message = "O valor do empréstimo é obrigatório")
    @DecimalMin(value = "100.0", message = "O valor mínimo para empréstimo é R$ 100")
    private Double amount;

    public ContractLoanRequest() {}

    public ContractLoanRequest(String loanType, Double amount) {
        this.loanType = loanType;
        this.amount = amount;
    }

    public String getLoanType() { return loanType; }
    public void setLoanType(String loanType) { this.loanType = loanType; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
}
