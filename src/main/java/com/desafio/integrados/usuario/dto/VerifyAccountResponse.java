package com.desafio.integrados.usuario.dto;

public class VerifyAccountResponse {

    private boolean verified;
    private String message;
    private String email;
    private String cardPin;

    public VerifyAccountResponse() {
    }

    public VerifyAccountResponse(boolean verified, String message, String email, String cardPin) {
        this.verified = verified;
        this.message = message;
        this.email = email;
        this.cardPin = cardPin;
    }

    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getCardPin() { return cardPin; }
    public void setCardPin(String cardPin) { this.cardPin = cardPin; }
}
