package com.desafio.integrados.cartao.domain;

import com.desafio.integrados.usuario.domain.User;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "card_invoices")
public class CardInvoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Double amount = 0.0;

    @Column(nullable = false)
    private String status = "OPEN"; // OPEN, PAID, OVERDUE

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "reference_month", nullable = false)
    private Integer referenceMonth;

    @Column(name = "reference_year", nullable = false)
    private Integer referenceYear;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public CardInvoice() {}

    public CardInvoice(Card card, User user, Double amount, LocalDate dueDate, Integer referenceMonth, Integer referenceYear) {
        this.card = card;
        this.user = user;
        this.amount = amount;
        this.dueDate = dueDate;
        this.referenceMonth = referenceMonth;
        this.referenceYear = referenceYear;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Card getCard() { return card; }
    public void setCard(Card card) { this.card = card; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
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
