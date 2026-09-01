package com.desafio.integrados.cartao.controller;

import com.desafio.integrados.autenticacao.security.CustomUserDetails;
import com.desafio.integrados.cartao.dto.CardInvoiceResponse;
import com.desafio.integrados.cartao.dto.CardResponse;
import com.desafio.integrados.cartao.service.CardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cards")
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping
    public ResponseEntity<List<CardResponse>> getMyCards(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.ok(cardService.getMyCards(userDetails.getId()));
    }

    @GetMapping("/invoices")
    public ResponseEntity<List<CardInvoiceResponse>> getMyInvoices(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.ok(cardService.getMyInvoices(userDetails.getId()));
    }
}
