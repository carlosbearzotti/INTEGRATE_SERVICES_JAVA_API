package com.desafio.integrados.fraude.dto;

import java.util.List;

public class FraudEvaluateResponse {

    private boolean approved;
    private int riskScore; // 0 to 100
    private String decision; // APPROVED, BLOCKED_FOR_REVIEW, REJECTED
    private Double distanceKm;
    private List<String> triggeredRules;
    private Long alertId;

    public FraudEvaluateResponse() {}

    public boolean isApproved() { return approved; }
    public void setApproved(boolean approved) { this.approved = approved; }
    public int getRiskScore() { return riskScore; }
    public void setRiskScore(int riskScore) { this.riskScore = riskScore; }
    public String getDecision() { return decision; }
    public void setDecision(String decision) { this.decision = decision; }
    public Double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }
    public List<String> getTriggeredRules() { return triggeredRules; }
    public void setTriggeredRules(List<String> triggeredRules) { this.triggeredRules = triggeredRules; }
    public Long getAlertId() { return alertId; }
    public void setAlertId(Long alertId) { this.alertId = alertId; }
}
