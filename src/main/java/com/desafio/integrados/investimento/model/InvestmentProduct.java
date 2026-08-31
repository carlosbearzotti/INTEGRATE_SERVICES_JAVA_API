package com.desafio.integrados.investimento.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_investments")
public class InvestmentProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 50)
    private String type; // CDB, LCI, LCA, TESOURO

    @Column(name = "index_name", nullable = false, length = 50)
    private String indexName; // CDI, SELIC, IPCA, PREFIXADO

    @Column(name = "rate_percent", nullable = false)
    private Double ratePercent; // Ex: 120.0 (120% do CDI)

    @Column(name = "min_amount", nullable = false)
    private Double minAmount;

    @Column(nullable = false, length = 50)
    private String liquidity; // DIARIA, 90_DIAS, NO_VENCIMENTO

    @Column(name = "grace_period_days", nullable = false)
    private Integer gracePeriodDays;

    @Column(name = "ir_exempt", nullable = false)
    private Boolean irExempt;

    @Column(nullable = false)
    private Boolean active = true;

    public InvestmentProduct() {}

    public InvestmentProduct(String name, String type, String indexName, Double ratePercent, Double minAmount, String liquidity, Integer gracePeriodDays, Boolean irExempt) {
        this.name = name;
        this.type = type;
        this.indexName = indexName;
        this.ratePercent = ratePercent;
        this.minAmount = minAmount;
        this.liquidity = liquidity;
        this.gracePeriodDays = gracePeriodDays;
        this.irExempt = irExempt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getIndexName() { return indexName; }
    public void setIndexName(String indexName) { this.indexName = indexName; }
    public Double getRatePercent() { return ratePercent; }
    public void setRatePercent(Double ratePercent) { this.ratePercent = ratePercent; }
    public Double getMinAmount() { return minAmount; }
    public void setMinAmount(Double minAmount) { this.minAmount = minAmount; }
    public String getLiquidity() { return liquidity; }
    public void setLiquidity(String liquidity) { this.liquidity = liquidity; }
    public Integer getGracePeriodDays() { return gracePeriodDays; }
    public void setGracePeriodDays(Integer gracePeriodDays) { this.gracePeriodDays = gracePeriodDays; }
    public Boolean getIrExempt() { return irExempt; }
    public void setIrExempt(Boolean irExempt) { this.irExempt = irExempt; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
