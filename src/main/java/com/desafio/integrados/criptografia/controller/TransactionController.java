package com.desafio.integrados.criptografia.controller;

import com.desafio.integrados.autenticacao.annotation.PublicEndpoint;
import com.desafio.integrados.criptografia.dto.TransactionDTO;
import com.desafio.integrados.criptografia.service.TransactionService;
import com.desafio.integrados.usuario.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.lang.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/transactions")
@PublicEndpoint
public class TransactionController {

    private final TransactionService service;
    private final UserRepository userRepository;

    public TransactionController(TransactionService service, UserRepository userRepository) {
        this.service = service;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<TransactionDTO> create(@RequestBody TransactionDTO dto, HttpServletRequest request) {
        Long authenticatedUserId = (Long) request.getAttribute("authenticatedUserId");
        if (authenticatedUserId != null && dto.getUserId() == null) {
            dto.setUserId(authenticatedUserId);
            if (dto.getUserDocument() == null || dto.getUserDocument().isBlank()) {
                userRepository.findById(authenticatedUserId).ifPresent(user -> dto.setUserDocument(user.getCpf()));
            }
        }
        return ResponseEntity.ok(service.create(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionDTO> findById(@PathVariable @NonNull Long id) {
        TransactionDTO result = service.findById(Objects.requireNonNull(id));
        if (result != null) {
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<TransactionDTO>> findAll(HttpServletRequest request) {
        Long authenticatedUserId = (Long) request.getAttribute("authenticatedUserId");
        if (authenticatedUserId != null) {
            return ResponseEntity.ok(service.findByUserId(authenticatedUserId));
        }
        return ResponseEntity.ok(service.findAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionDTO> update(@PathVariable @NonNull Long id, @RequestBody TransactionDTO dto) {
        TransactionDTO result = service.update(Objects.requireNonNull(id), dto);
        if (result != null) {
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @NonNull Long id) {
        service.delete(Objects.requireNonNull(id));
        return ResponseEntity.ok().build();
    }
}
