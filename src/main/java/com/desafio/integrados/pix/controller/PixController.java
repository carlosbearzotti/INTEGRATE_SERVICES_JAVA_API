package com.desafio.integrados.pix.controller;

import com.desafio.integrados.autenticacao.security.CustomUserDetails;
import com.desafio.integrados.pix.dto.PixKeyRequest;
import com.desafio.integrados.pix.dto.PixKeyResponse;
import com.desafio.integrados.pix.dto.PixTransactionResponse;
import com.desafio.integrados.pix.dto.PixTransferRequest;
import com.desafio.integrados.pix.service.PixService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pix")
public class PixController {

    private final PixService pixService;

    public PixController(PixService pixService) {
        this.pixService = pixService;
    }

    @PostMapping("/keys")
    public ResponseEntity<PixKeyResponse> registerKey(@Valid @RequestBody PixKeyRequest request,
                                                      @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        PixKeyResponse response = pixService.registerKey(userDetails.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/keys")
    public ResponseEntity<List<PixKeyResponse>> listMyKeys(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.ok(pixService.listMyKeys(userDetails.getId()));
    }

    @DeleteMapping("/keys/{id}")
    public ResponseEntity<Void> removeKey(@PathVariable Long id, 
                                          @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        pixService.removeKey(userDetails.getId(), id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/transfer")
    public ResponseEntity<PixTransactionResponse> transfer(@Valid @RequestBody PixTransferRequest request,
                                                           @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        PixTransactionResponse response = pixService.transfer(userDetails.getId(), request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<PixTransactionResponse>> getMyTransactions(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.ok(pixService.getMyTransactions(userDetails.getId()));
    }
}
