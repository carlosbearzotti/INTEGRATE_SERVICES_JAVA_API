package com.desafio.integrados.compliance.controller;

import com.desafio.integrados.autenticacao.annotation.PublicEndpoint;
import com.desafio.integrados.compliance.dto.LgpdAnonymizeRequest;
import com.desafio.integrados.compliance.dto.LgpdExportResponse;
import com.desafio.integrados.compliance.model.AuditLog;
import com.desafio.integrados.compliance.service.ComplianceService;
import com.desafio.integrados.usuario.domain.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/compliance")
public class ComplianceController {

    private final ComplianceService complianceService;

    public ComplianceController(ComplianceService complianceService) {
        this.complianceService = complianceService;
    }

    @GetMapping("/export-data")
    @PublicEndpoint
    public ResponseEntity<LgpdExportResponse> exportData(@RequestParam(defaultValue = "1") Long userId) {
        return ResponseEntity.ok(complianceService.exportData(userId));
    }

    @PostMapping("/anonymize")
    @PublicEndpoint
    public ResponseEntity<User> anonymize(@Valid @RequestBody LgpdAnonymizeRequest request) {
        return ResponseEntity.ok(complianceService.anonymizeUser(request));
    }

    @GetMapping("/audit-logs")
    @PublicEndpoint
    public ResponseEntity<List<AuditLog>> listAuditLogs() {
        return ResponseEntity.ok(complianceService.listAuditLogs());
    }
}
