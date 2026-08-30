package com.desafio.integrados.autenticacao.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FooBarController {

    @GetMapping("/foo-bar")
    public ResponseEntity<Void> getFooBar() {
        return ResponseEntity.noContent().build();
    }
}
