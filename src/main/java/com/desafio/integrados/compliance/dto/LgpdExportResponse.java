package com.desafio.integrados.compliance.dto;

import com.desafio.integrados.compliance.model.AuditLog;
import com.desafio.integrados.investimento.model.InvestmentPosition;
import com.desafio.integrados.usuario.dto.UserProfileResponse;

import java.time.LocalDateTime;
import java.util.List;

public class LgpdExportResponse {

    private String status = "SUCCESS";
    private LocalDateTime generatedAt = LocalDateTime.now();
    private String legalBasis = "Art. 18, II e V - Lei Geral de Proteção de Dados (LGPD 13.709/2018)";
    private UserProfileResponse personalData;
    private List<InvestmentPosition> investments;
    private List<AuditLog> auditTrail;

    public LgpdExportResponse() {}

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
    public String getLegalBasis() { return legalBasis; }
    public void setLegalBasis(String legalBasis) { this.legalBasis = legalBasis; }
    public UserProfileResponse getPersonalData() { return personalData; }
    public void setPersonalData(UserProfileResponse personalData) { this.personalData = personalData; }
    public List<InvestmentPosition> getInvestments() { return investments; }
    public void setInvestments(List<InvestmentPosition> investments) { this.investments = investments; }
    public List<AuditLog> getAuditTrail() { return auditTrail; }
    public void setAuditTrail(List<AuditLog> auditTrail) { this.auditTrail = auditTrail; }
}
