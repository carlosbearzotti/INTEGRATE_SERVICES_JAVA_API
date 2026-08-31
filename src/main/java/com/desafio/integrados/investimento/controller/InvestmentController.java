package com.desafio.integrados.investimento.controller;

import com.desafio.integrados.autenticacao.annotation.PublicEndpoint;
import com.desafio.integrados.investimento.dto.InvestmentApplyRequest;
import com.desafio.integrados.investimento.dto.InvestmentSimulationRequest;
import com.desafio.integrados.investimento.dto.InvestmentSimulationResponse;
import com.desafio.integrados.investimento.model.InvestmentPosition;
import com.desafio.integrados.investimento.model.InvestmentProduct;
import com.desafio.integrados.investimento.service.InvestmentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/investments")
public class InvestmentController {

    private final InvestmentService investmentService;

    public InvestmentController(InvestmentService investmentService) {
        this.investmentService = investmentService;
    }

    @GetMapping("/products")
    @PublicEndpoint
    public ResponseEntity<List<InvestmentProduct>> listProducts() {
        return ResponseEntity.ok(investmentService.listAvailableProducts());
    }

    @PostMapping("/simulate")
    @PublicEndpoint
    public ResponseEntity<InvestmentSimulationResponse> simulate(@Valid @RequestBody InvestmentSimulationRequest request) {
        return ResponseEntity.ok(investmentService.simulate(request));
    }

    @PostMapping("/apply")
    public ResponseEntity<InvestmentPosition> apply(@Valid @RequestBody InvestmentApplyRequest request, HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("authenticatedUserId");
        if (userId == null) userId = 1L; // Fallback para demonstração se chamado por consumer
        InvestmentPosition position = investmentService.apply(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(position);
    }

    @GetMapping("/my-funds")
    public ResponseEntity<List<InvestmentPosition>> getMyPositions(HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("authenticatedUserId");
        if (userId == null) userId = 1L;
        return ResponseEntity.ok(investmentService.listUserPositions(userId));
    }
}
