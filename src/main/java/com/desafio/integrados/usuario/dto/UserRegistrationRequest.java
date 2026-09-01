package com.desafio.integrados.usuario.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class UserRegistrationRequest {

    @NotBlank(message = "O nome é obrigatório.")
    private String name;

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "Formato de e-mail inválido.")
    private String email;

    @NotBlank(message = "A senha é obrigatória.")
    private String password;

    @NotBlank(message = "O CPF é obrigatório.")
    private String cpf;

    @NotNull(message = "A renda é obrigatória.")
    @Positive(message = "A renda deve ser um valor positivo.")
    private Double income;

    @NotNull(message = "A idade é obrigatória.")
    @Positive(message = "A idade deve ser um valor positivo.")
    private Integer age;

    private Double latitude;
    private Double longitude;
    private String cardPin;

    public UserRegistrationRequest() {
    }

    public UserRegistrationRequest(String name, String email, String password, String cpf, Double income, Integer age, Double latitude, Double longitude) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.cpf = cpf;
        this.income = income;
        this.age = age;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public Double getIncome() { return income; }
    public void setIncome(Double income) { this.income = income; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public String getCardPin() { return cardPin; }
    public void setCardPin(String cardPin) { this.cardPin = cardPin; }
}
