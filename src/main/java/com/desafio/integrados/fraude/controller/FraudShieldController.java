package com.desafio.integrados.fraude.controller;

import com.desafio.integrados.autenticacao.annotation.PublicEndpoint;
import com.desafio.integrados.fraude.dto.FraudEvaluateRequest;
import com.desafio.integrados.fraude.dto.FraudEvaluateResponse;
import com.desafio.integrados.fraude.dto.FraudReviewRequest;
import com.desafio.integrados.fraude.model.FraudAlert;
import com.desafio.integrados.fraude.model.FraudRule;
import com.desafio.integrados.fraude.service.FraudShieldService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fraud")
public class FraudShieldController {

    private final FraudShieldService fraudShieldService;

    public FraudShieldController(FraudShieldService fraudShieldService) {
        this.fraudShieldService = fraudShieldService;
    }

    @PostMapping("/evaluate")
    @PublicEndpoint
    public ResponseEntity<FraudEvaluateResponse> evaluate(@Valid @RequestBody FraudEvaluateRequest request) {
        return ResponseEntity.ok(fraudShieldService.evaluateTransaction(request));
    }

    @GetMapping("/alerts")
    @PublicEndpoint
    public ResponseEntity<List<FraudAlert>> listAlerts(@RequestParam(required = false) String status) {
        return ResponseEntity.ok(fraudShieldService.listAlerts(status));
    }

    @PostMapping("/review/{alertId}")
    @PublicEndpoint
    public ResponseEntity<FraudAlert> reviewAlert(@PathVariable Long alertId, @Valid @RequestBody FraudReviewRequest request) {
        return ResponseEntity.ok(fraudShieldService.reviewAlert(alertId, request));
    }

    @PutMapping("/rules")
    @PublicEndpoint
    public ResponseEntity<FraudRule> updateRules(@RequestBody FraudRule rule) {
        return ResponseEntity.ok(fraudShieldService.updateRules(rule));
    }
}
