package com.desafio.integrados.usuario.dto;

public class ForgotPasswordResponse {

    private String message;
    private String email;
    private String resetCode; // Código de 6 dígitos para o fluxo de simulação / middleware
    private boolean emailDispatched;

    public ForgotPasswordResponse() {}

    public ForgotPasswordResponse(String message, String email, String resetCode, boolean emailDispatched) {
        this.message = message;
        this.email = email;
        this.resetCode = resetCode;
        this.emailDispatched = emailDispatched;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getResetCode() { return resetCode; }
    public void setResetCode(String resetCode) { this.resetCode = resetCode; }
    public boolean isEmailDispatched() { return emailDispatched; }
    public void setEmailDispatched(boolean emailDispatched) { this.emailDispatched = emailDispatched; }
}
