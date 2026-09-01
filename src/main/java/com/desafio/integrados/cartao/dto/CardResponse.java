package com.desafio.integrados.cartao.dto;

import java.time.LocalDateTime;

public class CardResponse {

    private Long id;
    private String nameOnCard;
    private String cardNumber; // Apenas últimos 4 digitos
    private String validThru;
    private String cardType;
    private Double limitAmount;
    private Boolean active;
    private LocalDateTime createdAt;

    public CardResponse() {}

    public CardResponse(Long id, String nameOnCard, String cardNumber, String validThru, 
                        String cardType, Double limitAmount, Boolean active, LocalDateTime createdAt) {
        this.id = id;
        this.nameOnCard = nameOnCard;
        // Ocultar número do cartão por segurança, mostrando apenas os últimos 4 dígitos
        this.cardNumber = cardNumber != null && cardNumber.length() > 4 ? "****.****.****." + cardNumber.substring(cardNumber.length() - 4) : cardNumber;
        this.validThru = validThru;
        this.cardType = cardType;
        this.limitAmount = limitAmount;
        this.active = active;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNameOnCard() { return nameOnCard; }
    public void setNameOnCard(String nameOnCard) { this.nameOnCard = nameOnCard; }
    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
    public String getValidThru() { return validThru; }
    public void setValidThru(String validThru) { this.validThru = validThru; }
    public String getCardType() { return cardType; }
    public void setCardType(String cardType) { this.cardType = cardType; }
    public Double getLimitAmount() { return limitAmount; }
    public void setLimitAmount(Double limitAmount) { this.limitAmount = limitAmount; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
