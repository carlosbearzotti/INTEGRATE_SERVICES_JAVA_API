package com.desafio.integrados.usuario.dto;

public class UserProfileResponse {

    private Long id;
    private String name;
    private String email;
    private String cpf;
    private Double income;
    private Integer age;
    private Double latitude;
    private Double longitude;
    private String role;

    public UserProfileResponse() {
    }

    public UserProfileResponse(Long id, String name, String email, String cpf, Double income, Integer age, Double latitude, Double longitude) {
        this(id, name, email, cpf, income, age, latitude, longitude, "ROLE_CUSTOMER");
    }

    public UserProfileResponse(Long id, String name, String email, String cpf, Double income, Integer age, Double latitude, Double longitude, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.cpf = cpf;
        this.income = income;
        this.age = age;
        this.latitude = latitude;
        this.longitude = longitude;
        this.role = role != null ? role : "ROLE_CUSTOMER";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
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
}
