package com.desafio.integrados.fraude.dto;

import jakarta.validation.constraints.NotNull;

public class FraudEvaluateRequest {

    private Long userId;

    @NotNull(message = "O valor da transação é obrigatório")
    private Double amount;

    private Double originLat;
    private Double originLng;

    public FraudEvaluateRequest() {}

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public Double getOriginLat() { return originLat; }
    public void setOriginLat(Double originLat) { this.originLat = originLat; }
    public Double getOriginLng() { return originLng; }
    public void setOriginLng(Double originLng) { this.originLng = originLng; }
}
