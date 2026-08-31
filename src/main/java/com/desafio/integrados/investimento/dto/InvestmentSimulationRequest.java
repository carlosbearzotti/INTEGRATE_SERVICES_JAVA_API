package com.desafio.integrados.investimento.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class InvestmentSimulationRequest {

    @NotNull(message = "O valor inicial é obrigatório")
    @Min(value = 1, message = "O valor inicial deve ser maior que zero")
    private Double initialAmount;

    private Double monthlyContribution = 0.0;

    @NotNull(message = "O prazo em meses é obrigatório")
    @Min(value = 1, message = "O prazo deve ser de no mínimo 1 mês")
    private Integer months;

    private Double cdiRateAnnual = 10.75; // Taxa CDI Anual padrão (10.75%)

    private Double productRatePercent = 120.0; // 120% do CDI

    private Boolean irExempt = false; // Isenção de IR (LCI/LCA)

    public InvestmentSimulationRequest() {}

    public Double getInitialAmount() { return initialAmount; }
    public void setInitialAmount(Double initialAmount) { this.initialAmount = initialAmount; }
    public Double getMonthlyContribution() { return monthlyContribution; }
    public void setMonthlyContribution(Double monthlyContribution) { this.monthlyContribution = monthlyContribution; }
    public Integer getMonths() { return months; }
    public void setMonths(Integer months) { this.months = months; }
    public Double getCdiRateAnnual() { return cdiRateAnnual; }
    public void setCdiRateAnnual(Double cdiRateAnnual) { this.cdiRateAnnual = cdiRateAnnual; }
    public Double getProductRatePercent() { return productRatePercent; }
    public void setProductRatePercent(Double productRatePercent) { this.productRatePercent = productRatePercent; }
    public Boolean getIrExempt() { return irExempt; }
    public void setIrExempt(Boolean irExempt) { this.irExempt = irExempt; }
}
