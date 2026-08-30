package com.desafio.integrados.autenticacao.service.impl;

import com.desafio.integrados.autenticacao.service.JwtService;
import com.desafio.integrados.autenticacao.service.TokenValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TokenValidationServiceImpl implements TokenValidationService {

    private static final String BEARER_PREFIX = "Bearer ";

    private final Set<String> validTokens;
    private final JwtService jwtService;

    @Autowired
    public TokenValidationServiceImpl(
            @Value("${auth.valid-tokens:vYQIYxOpyfr==}") String[] validTokensConfig,
            @Autowired(required = false) JwtService jwtService
    ) {
        if (validTokensConfig != null && validTokensConfig.length > 0) {
            this.validTokens = Arrays.stream(validTokensConfig)
                    .map(String::trim)
                    .filter(token -> !token.isEmpty())
                    .collect(Collectors.toUnmodifiableSet());
        } else {
            this.validTokens = Collections.singleton("vYQIYxOpyfr==");
        }
        this.jwtService = jwtService;
    }

    public TokenValidationServiceImpl(String[] validTokensConfig) {
        this(validTokensConfig, null);
    }

    @Override
    public boolean isValid(String authorizationHeader) {
        String token = extractToken(authorizationHeader);
        if (token == null || token.isBlank()) {
            return false;
        }

        if (validTokens.contains(token)) {
            return true;
        }

        if (jwtService != null && jwtService.verifyToken(token) != null) {
            return true;
        }

        return false;
    }

    @Override
    public String extractToken(String authorizationHeader) {
        if (authorizationHeader == null) {
            return null;
        }

        String trimmed = authorizationHeader.trim();
        if (trimmed.isEmpty()) {
            return null;
        }

        if (trimmed.equalsIgnoreCase("Bearer")) {
            return null;
        }

        if (trimmed.regionMatches(true, 0, BEARER_PREFIX, 0, BEARER_PREFIX.length())) {
            String candidate = trimmed.substring(BEARER_PREFIX.length()).trim();
            return candidate.isEmpty() ? null : candidate;
        }

        return trimmed;
    }
}
