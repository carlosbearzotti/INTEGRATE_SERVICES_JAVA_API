package com.desafio.integrados.emprestimo.dto;

import com.desafio.integrados.emprestimo.domain.Customer;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class CustomerLoanRequest {

    @NotNull(message = "O campo 'age' é obrigatório.")
    @Min(value = 0, message = "A idade deve ser maior ou igual a zero.")
    private Integer age;

    @NotBlank(message = "O campo 'cpf' é obrigatório.")
    private String cpf;

    @NotBlank(message = "O campo 'name' é obrigatório.")
    private String name;

    @NotNull(message = "O campo 'income' é obrigatório.")
    @DecimalMin(value = "0.0", inclusive = true, message = "O rendimento deve ser maior ou igual a zero.")
    private BigDecimal income;

    @NotBlank(message = "O campo 'location' é obrigatório.")
    private String location;

    public CustomerLoanRequest() {
    }

    public CustomerLoanRequest(Integer age, String cpf, String name, BigDecimal income, String location) {
        this.age = age;
        this.cpf = cpf;
        this.name = name;
        this.income = income;
        this.location = location;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getIncome() {
        return income;
    }

    public void setIncome(BigDecimal income) {
        this.income = income;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Customer toDomain() {
        return new Customer(this.name, this.cpf, this.age, this.income, this.location);
    }
}
