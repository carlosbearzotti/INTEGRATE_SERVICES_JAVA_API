package com.desafio.integrados.emprestimo.domain;

import com.desafio.integrados.usuario.domain.User;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "loans")
public class LoanContract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "loan_type", nullable = false)
    private String loanType = "PERSONAL";

    @Column(nullable = false)
    private Double amount;

    @Column(name = "amount_with_interest", nullable = false)
    private Double amountWithInterest;

    @Column(nullable = false)
    private Integer installments;

    @Column(name = "installment_value", nullable = false)
    private Double installmentValue;

    @Column(name = "first_installment_date", nullable = false)
    private LocalDate firstInstallmentDate;

    @Column(name = "total_interest", nullable = false)
    private Double totalInterest;

    @Column(nullable = false)
    private Double iof;

    @Column(nullable = false)
    private Double rate;

    @Column(nullable = false)
    private String status = "ACTIVE"; // ACTIVE, PAID, DEFAULTED

    @Column(name = "contracted_at", nullable = false, updatable = false)
    private LocalDateTime contractedAt = LocalDateTime.now();

    public LoanContract() {}

    public LoanContract(User user, String loanType, Double amount, Double amountWithInterest, Integer installments, 
                        Double installmentValue, LocalDate firstInstallmentDate, Double totalInterest, Double iof, Double rate) {
        this.user = user;
        this.loanType = loanType;
        this.amount = amount;
        this.amountWithInterest = amountWithInterest;
        this.installments = installments;
        this.installmentValue = installmentValue;
        this.firstInstallmentDate = firstInstallmentDate;
        this.totalInterest = totalInterest;
        this.iof = iof;
        this.rate = rate;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
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
    public Double getTotalInterest() { return totalInterest; }
    public void setTotalInterest(Double totalInterest) { this.totalInterest = totalInterest; }
    public Double getIof() { return iof; }
    public void setIof(Double iof) { this.iof = iof; }
    public Double getRate() { return rate; }
    public void setRate(Double rate) { this.rate = rate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getContractedAt() { return contractedAt; }
    public void setContractedAt(LocalDateTime contractedAt) { this.contractedAt = contractedAt; }
}
