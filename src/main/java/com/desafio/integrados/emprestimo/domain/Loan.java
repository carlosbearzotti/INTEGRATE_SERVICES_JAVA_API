package com.desafio.integrados.emprestimo.domain;

import java.util.Objects;

public class Loan {

    private final LoanType type;
    private final int interestRate;

    public Loan(LoanType type, int interestRate) {
        this.type = type;
        this.interestRate = interestRate;
    }

    public Loan(LoanType type) {
        this(type, type.getInterestRate());
    }

    public LoanType getType() {
        return type;
    }

    public int getInterestRate() {
        return interestRate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Loan loan = (Loan) o;
        return interestRate == loan.interestRate && type == loan.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, interestRate);
    }

    @Override
    public String toString() {
        return "Loan{" +
                "type=" + type +
                ", interestRate=" + interestRate +
                '}';
    }
}
