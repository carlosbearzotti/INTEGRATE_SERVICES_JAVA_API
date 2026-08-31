package com.desafio.integrados.webhook.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class WebhookTestDispatchRequest {

    @NotNull(message = "O ID da subscrição é obrigatório")
    private Long subscriptionId;

    @NotBlank(message = "O tipo de evento é obrigatório")
    private String eventType; // transaction.completed, loan.contracted, url.milestone_reached

    private String samplePayload;

    public WebhookTestDispatchRequest() {}

    public Long getSubscriptionId() { return subscriptionId; }
    public void setSubscriptionId(Long subscriptionId) { this.subscriptionId = subscriptionId; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public String getSamplePayload() { return samplePayload; }
    public void setSamplePayload(String samplePayload) { this.samplePayload = samplePayload; }
}
