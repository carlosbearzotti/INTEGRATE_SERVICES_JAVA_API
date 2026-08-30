package com.desafio.integrados.autenticacao.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.desafio.integrados.usuario.domain.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class JwtService {

    @Value("${auth.jwt.secret:default-secret-key-for-jwt-signing-1234567890}")
    private String secret;

    @Value("${auth.jwt.issuer:integrados-api}")
    private String issuer;

    public String generateToken(User user) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.create()
                .withIssuer(issuer)
                .withSubject(user.getEmail())
                .withClaim("userId", user.getId())
                .withClaim("name", user.getName())
                .withIssuedAt(new Date())
                .withExpiresAt(Date.from(Instant.now().plus(24, ChronoUnit.HOURS)))
                .sign(algorithm);
    }

    public DecodedJWT verifyToken(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer(issuer)
                    .build()
                    .verify(token);
        } catch (JWTVerificationException e) {
            return null;
        }
    }

    public Long extractUserId(String token) {
        DecodedJWT decodedJWT = verifyToken(token);
        if (decodedJWT != null && !decodedJWT.getClaim("userId").isNull()) {
            return decodedJWT.getClaim("userId").asLong();
        }
        return null;
    }

    public String extractSubject(String token) {
        DecodedJWT decodedJWT = verifyToken(token);
        return decodedJWT != null ? decodedJWT.getSubject() : null;
    }
}
