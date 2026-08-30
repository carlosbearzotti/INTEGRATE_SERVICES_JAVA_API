package com.desafio.integrados.senhasegura.controller;

import com.desafio.integrados.senhasegura.dto.PasswordValidationRequest;
import com.desafio.integrados.senhasegura.service.PasswordValidationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/validate-password", "/api/validate-password"})
public class PasswordValidationController {

    private final PasswordValidationService passwordValidationService;

    public PasswordValidationController(PasswordValidationService passwordValidationService) {
        this.passwordValidationService = passwordValidationService;
    }

    @PostMapping
    public ResponseEntity<Void> validatePassword(@RequestBody(required = false) PasswordValidationRequest request) {
        passwordValidationService.validate(request);
        return ResponseEntity.noContent().build();
    }
}
