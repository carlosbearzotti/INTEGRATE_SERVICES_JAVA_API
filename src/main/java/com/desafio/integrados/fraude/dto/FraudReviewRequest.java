package com.desafio.integrados.fraude.dto;

import jakarta.validation.constraints.NotBlank;

public class FraudReviewRequest {

    @NotBlank(message = "A decisão deve ser APPROVED ou REJECTED")
    private String decision; // APPROVED, REJECTED

    private String reviewerName;

    public FraudReviewRequest() {}

    public String getDecision() { return decision; }
    public void setDecision(String decision) { this.decision = decision; }
    public String getReviewerName() { return reviewerName; }
    public void setReviewerName(String reviewerName) { this.reviewerName = reviewerName; }
}
