package com.desafio.integrados.emprestimo.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ContractLoanResponse {

    private Long id;
    private String loanType;
    private Double amount;
    private Double amountWithInterest;
    private Integer installments;
    private Double installmentValue;
    private LocalDate firstInstallmentDate;
    private Double rate;
    private String status;
    private LocalDateTime contractedAt;

    public ContractLoanResponse() {}

    public ContractLoanResponse(Long id, String loanType, Double amount, Double amountWithInterest, 
                                Integer installments, Double installmentValue, LocalDate firstInstallmentDate, 
                                Double rate, String status, LocalDateTime contractedAt) {
        this.id = id;
        this.loanType = loanType;
        this.amount = amount;
        this.amountWithInterest = amountWithInterest;
        this.installments = installments;
        this.installmentValue = installmentValue;
        this.firstInstallmentDate = firstInstallmentDate;
        this.rate = rate;
        this.status = status;
        this.contractedAt = contractedAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getLoanType() { return loanType; }
    public void setLoanType(String loanType) { this.loanType = loanType; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public Double getAmountWithInterest() { return amountWithInterest; }
    public void setAmountWithInterest(Double amountWithInterest) { this.amountWithInterest = amountWithInterest; }
    public Integer getInstallments() { return installments; }
    public void setInstallments(Integer installments) { this.installments = installments; }
    public Double getInstallmentValue() { return installmentValue; }
    public void setInstallmentValue(Double installmentValue) { this.installmentValue = installmentValue; }
    public LocalDate getFirstInstallmentDate() { return firstInstallmentDate; }
    public void setFirstInstallmentDate(LocalDate firstInstallmentDate) { this.firstInstallmentDate = firstInstallmentDate; }
    public Double getRate() { return rate; }
    public void setRate(Double rate) { this.rate = rate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getContractedAt() { return contractedAt; }
    public void setContractedAt(LocalDateTime contractedAt) { this.contractedAt = contractedAt; }
}
