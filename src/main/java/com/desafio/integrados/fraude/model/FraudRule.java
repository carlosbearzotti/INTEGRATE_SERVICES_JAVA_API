package com.desafio.integrados.fraude.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_fraud_rules")
public class FraudRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rule_name", nullable = false, length = 150)
    private String ruleName;

    @Column(name = "max_distance_km", nullable = false)
    private Double maxDistanceKm = 500.0;

    @Column(name = "max_amount", nullable = false)
    private Double maxAmount = 50000.0;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    public FraudRule() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRuleName() { return ruleName; }
    public void setRuleName(String ruleName) { this.ruleName = ruleName; }
    public Double getMaxDistanceKm() { return maxDistanceKm; }
    public void setMaxDistanceKm(Double maxDistanceKm) { this.maxDistanceKm = maxDistanceKm; }
    public Double getMaxAmount() { return maxAmount; }
    public void setMaxAmount(Double maxAmount) { this.maxAmount = maxAmount; }
    public Boolean getActive() { return isActive; }
    public void setActive(Boolean active) { isActive = active; }
}
