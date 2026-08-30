package com.desafio.integrados.emprestimo.dto;

import com.desafio.integrados.emprestimo.domain.Loan;
import com.desafio.integrados.emprestimo.domain.LoanType;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class LoanResponse {

    private LoanType type;

    @JsonProperty("interest_rate")
    private Integer interestRate;

    public LoanResponse() {
    }

    public LoanResponse(LoanType type, Integer interestRate) {
        this.type = type;
        this.interestRate = interestRate;
    }

    public static LoanResponse fromDomain(Loan loan) {
        return new LoanResponse(loan.getType(), loan.getInterestRate());
    }

    public LoanType getType() {
        return type;
    }

    public void setType(LoanType type) {
        this.type = type;
    }

    public Integer getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(Integer interestRate) {
        this.interestRate = interestRate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoanResponse that = (LoanResponse) o;
        return type == that.type && Objects.equals(interestRate, that.interestRate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, interestRate);
    }

    @Override
    public String toString() {
        return "LoanResponse{" +
                "type=" + type +
                ", interestRate=" + interestRate +
                '}';
    }
}
