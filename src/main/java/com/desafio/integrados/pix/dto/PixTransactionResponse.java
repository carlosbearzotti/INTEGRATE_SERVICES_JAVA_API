package com.desafio.integrados.pix.dto;

import java.time.LocalDateTime;

public class PixTransactionResponse {

    private String txid;
    private String senderName;
    private String receiverName;
    private Double amount;
    private String description;
    private String status;
    private LocalDateTime timestamp;

    public PixTransactionResponse() {}

    public PixTransactionResponse(String txid, String senderName, String receiverName, 
                                  Double amount, String description, String status, LocalDateTime timestamp) {
        this.txid = txid;
        this.senderName = senderName;
        this.receiverName = receiverName;
        this.amount = amount;
        this.description = description;
        this.status = status;
        this.timestamp = timestamp;
    }

    public String getTxid() { return txid; }
    public void setTxid(String txid) { this.txid = txid; }
    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }
    public String getReceiverName() { return receiverName; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
