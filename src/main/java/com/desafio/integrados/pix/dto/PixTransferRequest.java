package com.desafio.integrados.pix.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PixTransferRequest {

    @NotBlank(message = "A chave de destino não pode estar vazia")
    private String destinationKey;

    @NotNull(message = "O valor da transferência é obrigatório")
    @DecimalMin(value = "0.01", message = "O valor da transferência deve ser maior que zero")
    private Double amount;

    private String description;

    public PixTransferRequest() {}

    public PixTransferRequest(String destinationKey, Double amount, String description) {
        this.destinationKey = destinationKey;
        this.amount = amount;
        this.description = description;
    }

    public String getDestinationKey() { return destinationKey; }
    public void setDestinationKey(String destinationKey) { this.destinationKey = destinationKey; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
