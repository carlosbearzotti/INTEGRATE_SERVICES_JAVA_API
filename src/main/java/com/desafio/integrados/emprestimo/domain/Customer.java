package com.desafio.integrados.emprestimo.domain;

import java.math.BigDecimal;
import java.util.Objects;

public class Customer {

    private final String name;
    private final String cpf;
    private final int age;
    private final BigDecimal income;
    private final String location;

    public Customer(String name, String cpf, int age, BigDecimal income, String location) {
        this.name = name;
        this.cpf = cpf;
        this.age = age;
        this.income = income != null ? income : BigDecimal.ZERO;
        this.location = location != null ? location.trim() : "";
    }

    public String getName() {
        return name;
    }

    public String getCpf() {
        return cpf;
    }

    public int getAge() {
        return age;
    }

    public BigDecimal getIncome() {
        return income;
    }

    public String getLocation() {
        return location;
    }

    public boolean isIncomeLessThanOrEqualTo(BigDecimal threshold) {
        return this.income.compareTo(threshold) <= 0;
    }

    public boolean isIncomeGreaterThanOrEqualTo(BigDecimal threshold) {
        return this.income.compareTo(threshold) >= 0;
    }

    public boolean isIncomeBetween(BigDecimal min, BigDecimal max) {
        return this.income.compareTo(min) >= 0 && this.income.compareTo(max) <= 0;
    }

    public boolean isAgeLessThan(int targetAge) {
        return this.age < targetAge;
    }

    public boolean isLocation(String targetLocation) {
        return this.location.equalsIgnoreCase(targetLocation);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return age == customer.age &&
                Objects.equals(name, customer.name) &&
                Objects.equals(cpf, customer.cpf) &&
                Objects.equals(income, customer.income) &&
                Objects.equals(location, customer.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, cpf, age, income, location);
    }
}
