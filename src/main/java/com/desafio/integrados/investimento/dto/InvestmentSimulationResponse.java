package com.desafio.integrados.investimento.dto;

import java.util.List;

public class InvestmentSimulationResponse {

    private Double totalInvested;
    private Double grossYield;
    private Double grossTotal;
    private Double irTaxAmount;
    private Double irTaxRate;
    private Double netTotal;
    private Double netYield;
    private Double benchmarkPoupanca;
    private Double profitOverPoupanca;
    private List<MonthlyEvolution> evolution;

    public static class MonthlyEvolution {
        private int month;
        private Double invested;
        private Double gross;
        private Double net;

        public MonthlyEvolution(int month, Double invested, Double gross, Double net) {
            this.month = month;
            this.invested = invested;
            this.gross = gross;
            this.net = net;
        }

        public int getMonth() { return month; }
        public Double getInvested() { return invested; }
        public Double getGross() { return gross; }
        public Double getNet() { return net; }
    }

    public InvestmentSimulationResponse() {}

    public Double getTotalInvested() { return totalInvested; }
    public void setTotalInvested(Double totalInvested) { this.totalInvested = totalInvested; }
    public Double getGrossYield() { return grossYield; }
    public void setGrossYield(Double grossYield) { this.grossYield = grossYield; }
    public Double getGrossTotal() { return grossTotal; }
    public void setGrossTotal(Double grossTotal) { this.grossTotal = grossTotal; }
    public Double getIrTaxAmount() { return irTaxAmount; }
    public void setIrTaxAmount(Double irTaxAmount) { this.irTaxAmount = irTaxAmount; }
    public Double getIrTaxRate() { return irTaxRate; }
    public void setIrTaxRate(Double irTaxRate) { this.irTaxRate = irTaxRate; }
    public Double getNetTotal() { return netTotal; }
    public void setNetTotal(Double netTotal) { this.netTotal = netTotal; }
    public Double getNetYield() { return netYield; }
    public void setNetYield(Double netYield) { this.netYield = netYield; }
    public Double getBenchmarkPoupanca() { return benchmarkPoupanca; }
    public void setBenchmarkPoupanca(Double benchmarkPoupanca) { this.benchmarkPoupanca = benchmarkPoupanca; }
    public Double getProfitOverPoupanca() { return profitOverPoupanca; }
    public void setProfitOverPoupanca(Double profitOverPoupanca) { this.profitOverPoupanca = profitOverPoupanca; }
    public List<MonthlyEvolution> getEvolution() { return evolution; }
    public void setEvolution(List<MonthlyEvolution> evolution) { this.evolution = evolution; }
}
