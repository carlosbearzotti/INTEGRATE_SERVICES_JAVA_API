package com.desafio.integrados.compliance.dto;

import jakarta.validation.constraints.NotNull;

public class LgpdAnonymizeRequest {

    @NotNull(message = "O ID do usuário a anonimizar é obrigatório")
    private Long userId;

    private String reason = "Direito ao esquecimento solicitado pelo titular (Art. 18, VI da LGPD)";

    public LgpdAnonymizeRequest() {}

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
