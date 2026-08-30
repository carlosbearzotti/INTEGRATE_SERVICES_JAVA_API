package com.desafio.integrados.autenticacao.controller;

import com.desafio.integrados.autenticacao.annotation.PublicEndpoint;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/recursos")
public class SampleProtectedController {

    @GetMapping("/protegido")
    public ResponseEntity<Map<String, String>> getRecursoProtegido() {
        return ResponseEntity.ok(Map.of("status", "sucesso", "mensagem", "Recurso protegido acessado com autenticação válida."));
    }

    @PublicEndpoint
    @GetMapping("/publico")
    public ResponseEntity<Map<String, String>> getRecursoPublico() {
        return ResponseEntity.ok(Map.of("status", "sucesso", "mensagem", "Recurso público acessado sem necessidade de token."));
    }
}
