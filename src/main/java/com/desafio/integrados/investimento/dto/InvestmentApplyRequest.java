package com.desafio.integrados.investimento.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class InvestmentApplyRequest {

    @NotNull(message = "O ID do produto é obrigatório")
    private Long productId;

    @NotNull(message = "O valor da aplicação é obrigatório")
    @Min(value = 1, message = "O valor deve ser de no mínimo R$ 1,00")
    private Double amount;

    public InvestmentApplyRequest() {}

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
}
