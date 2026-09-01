package com.desafio.integrados.usuario.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String cpf;

    @Column(nullable = false)
    private Double income;

    @Column(nullable = false)
    private Integer age;

    @Column
    private Double latitude;

    @Column
    private Double longitude;

    @Column(nullable = false)
    private String role = "ROLE_CUSTOMER";

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified = false;

    public User() {
    }

    public User(String name, String email, String password, String cpf, Double income, Integer age, Double latitude, Double longitude) {
        this(name, email, password, cpf, income, age, latitude, longitude, "ROLE_CUSTOMER");
    }

    public User(String name, String email, String password, String cpf, Double income, Integer age, Double latitude, Double longitude, String role) {
        this(name, email, password, cpf, income, age, latitude, longitude, role, "ROLE_ADMIN".equalsIgnoreCase(role));
    }

    public User(String name, String email, String password, String cpf, Double income, Integer age, Double latitude, Double longitude, String role, boolean emailVerified) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.cpf = cpf;
        this.income = income;
        this.age = age;
        this.latitude = latitude;
        this.longitude = longitude;
        this.role = role != null ? role : "ROLE_CUSTOMER";
        this.emailVerified = emailVerified;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public boolean isEmailVerified() { return emailVerified; }
    public void setEmailVerified(boolean emailVerified) { this.emailVerified = emailVerified; }
}
