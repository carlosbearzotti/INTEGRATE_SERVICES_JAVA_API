package com.desafio.integrados.cartao.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CardInvoiceResponse {

    private Long id;
    private Long cardId;
    private Double amount;
    private String status;
    private LocalDate dueDate;
    private Integer referenceMonth;
    private Integer referenceYear;
    private LocalDateTime createdAt;

    public CardInvoiceResponse() {}

    public CardInvoiceResponse(Long id, Long cardId, Double amount, String status, 
                               LocalDate dueDate, Integer referenceMonth, Integer referenceYear, LocalDateTime createdAt) {
        this.id = id;
        this.cardId = cardId;
        this.amount = amount;
        this.status = status;
        this.dueDate = dueDate;
        this.referenceMonth = referenceMonth;
        this.referenceYear = referenceYear;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCardId() { return cardId; }
    public void setCardId(Long cardId) { this.cardId = cardId; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public Integer getReferenceMonth() { return referenceMonth; }
    public void setReferenceMonth(Integer referenceMonth) { this.referenceMonth = referenceMonth; }
    public Integer getReferenceYear() { return referenceYear; }
    public void setReferenceYear(Integer referenceYear) { this.referenceYear = referenceYear; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
