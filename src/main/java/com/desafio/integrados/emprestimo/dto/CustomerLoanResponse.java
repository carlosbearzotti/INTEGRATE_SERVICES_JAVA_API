package com.desafio.integrados.emprestimo.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CustomerLoanResponse {

    private String customer;
    private List<LoanResponse> loans = new ArrayList<>();

    public CustomerLoanResponse() {
    }

    public CustomerLoanResponse(String customer, List<LoanResponse> loans) {
        this.customer = customer;
        this.loans = loans != null ? loans : new ArrayList<>();
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public List<LoanResponse> getLoans() {
        return loans;
    }

    public void setLoans(List<LoanResponse> loans) {
        this.loans = loans != null ? loans : new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerLoanResponse that = (CustomerLoanResponse) o;
        return Objects.equals(customer, that.customer) && Objects.equals(loans, that.loans);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customer, loans);
    }

    @Override
    public String toString() {
        return "CustomerLoanResponse{" +
                "customer='" + customer + '\'' +
                ", loans=" + loans +
                '}';
    }
}
