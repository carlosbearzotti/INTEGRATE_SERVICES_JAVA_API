package com.desafio.integrados.pix.domain;

import com.desafio.integrados.usuario.domain.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pix_transactions")
public class PixTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private User receiver;

    @Column(name = "sender_document")
    private String senderDocument;

    @Column(name = "receiver_document")
    private String receiverDocument;

    @Column(nullable = false)
    private Double amount;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private String status = "COMPLETED";

    @Column(unique = true)
    private String txid;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public PixTransaction() {}

    public PixTransaction(User sender, User receiver, String senderDocument, String receiverDocument, 
                          Double amount, String description, String txid) {
        this.sender = sender;
        this.receiver = receiver;
        this.senderDocument = senderDocument;
        this.receiverDocument = receiverDocument;
        this.amount = amount;
        this.description = description;
        this.txid = txid;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getSender() { return sender; }
    public void setSender(User sender) { this.sender = sender; }
    public User getReceiver() { return receiver; }
    public void setReceiver(User receiver) { this.receiver = receiver; }
    public String getSenderDocument() { return senderDocument; }
    public void setSenderDocument(String senderDocument) { this.senderDocument = senderDocument; }
    public String getReceiverDocument() { return receiverDocument; }
    public void setReceiverDocument(String receiverDocument) { this.receiverDocument = receiverDocument; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getTxid() { return txid; }
    public void setTxid(String txid) { this.txid = txid; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
