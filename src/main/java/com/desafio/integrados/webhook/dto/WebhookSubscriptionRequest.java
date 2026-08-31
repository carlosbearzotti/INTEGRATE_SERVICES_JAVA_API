package com.desafio.integrados.webhook.dto;

import jakarta.validation.constraints.NotBlank;

public class WebhookSubscriptionRequest {

    @NotBlank(message = "A URL de callback é obrigatória")
    private String url;

    @NotBlank(message = "Pelo menos um tipo de evento deve ser informado")
    private String eventTypes; // 'transaction.completed,loan.contracted,url.milestone_reached'

    private String secretKey;

    public WebhookSubscriptionRequest() {}

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getEventTypes() { return eventTypes; }
    public void setEventTypes(String eventTypes) { this.eventTypes = eventTypes; }
    public String getSecretKey() { return secretKey; }
    public void setSecretKey(String secretKey) { this.secretKey = secretKey; }
}
