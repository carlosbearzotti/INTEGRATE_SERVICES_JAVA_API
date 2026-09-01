package com.desafio.integrados.cartao.domain;

import com.desafio.integrados.usuario.domain.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "cards")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "name_on_card", nullable = false)
    private String nameOnCard;

    @Column(name = "card_number", nullable = false, unique = true)
    private String cardNumber;

    @Column(name = "valid_thru", nullable = false)
    private String validThru;

    @Column(nullable = false)
    private String cvv;

    @Column(name = "card_type", nullable = false)
    private String cardType = "PHYSICAL"; // PHYSICAL, VIRTUAL

    @Column(name = "limit_amount", nullable = false)
    private Double limitAmount = 0.0;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Card() {}

    public Card(User user, String nameOnCard, String cardNumber, String validThru, String cvv, String cardType, Double limitAmount) {
        this.user = user;
        this.nameOnCard = nameOnCard;
        this.cardNumber = cardNumber;
        this.validThru = validThru;
        this.cvv = cvv;
        this.cardType = cardType;
        this.limitAmount = limitAmount;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getNameOnCard() { return nameOnCard; }
    public void setNameOnCard(String nameOnCard) { this.nameOnCard = nameOnCard; }
    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
    public String getValidThru() { return validThru; }
    public void setValidThru(String validThru) { this.validThru = validThru; }
    public String getCvv() { return cvv; }
    public void setCvv(String cvv) { this.cvv = cvv; }
    public String getCardType() { return cardType; }
    public void setCardType(String cardType) { this.cardType = cardType; }
    public Double getLimitAmount() { return limitAmount; }
    public void setLimitAmount(Double limitAmount) { this.limitAmount = limitAmount; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
