package com.desafio.integrados.agendamento.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class ScheduleTransferRequest {

    @NotBlank(message = "O nome do favorecido é obrigatório")
    private String recipientName;

    @NotBlank(message = "O documento/chave Pix do favorecido é obrigatório")
    private String recipientDocument;

    @NotNull(message = "O valor da transferência é obrigatório")
    @Min(value = 1, message = "O valor deve ser de no mínimo R$ 1,00")
    private Double amount;

    private String transferType = "PIX"; // PIX, TED, BOLETO

    @NotNull(message = "A data de agendamento é obrigatória")
    private LocalDate scheduledFor;

    public ScheduleTransferRequest() {}

    public String getRecipientName() { return recipientName; }
    public void setRecipientName(String recipientName) { this.recipientName = recipientName; }
    public String getRecipientDocument() { return recipientDocument; }
    public void setRecipientDocument(String recipientDocument) { this.recipientDocument = recipientDocument; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public String getTransferType() { return transferType; }
    public void setTransferType(String transferType) { this.transferType = transferType; }
    public LocalDate getScheduledFor() { return scheduledFor; }
    public void setScheduledFor(LocalDate scheduledFor) { this.scheduledFor = scheduledFor; }
}
