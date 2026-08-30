package com.desafio.integrados.autenticacao.interceptor;

import com.desafio.integrados.autenticacao.annotation.PublicEndpoint;
import com.desafio.integrados.autenticacao.exception.InvalidTokenException;
import com.desafio.integrados.autenticacao.service.JwtService;
import com.desafio.integrados.autenticacao.service.TokenValidationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    private final TokenValidationService tokenValidationService;
    private final JwtService jwtService;

    @Autowired
    public AuthenticationInterceptor(@Autowired(required = false) TokenValidationService tokenValidationService,
                                     @Autowired(required = false) JwtService jwtService) {
        this.tokenValidationService = tokenValidationService;
        this.jwtService = jwtService;
    }

    public AuthenticationInterceptor(TokenValidationService tokenValidationService) {
        this(tokenValidationService, null);
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        // Permite requisições pre-flight CORS
        String method = request.getMethod();
        if (method != null && HttpMethod.OPTIONS.matches(method)) {
            return true;
        }

        // Se o handler for um método de controlador, verifica se é uma rota pública
        if (handler instanceof HandlerMethod handlerMethod) {
            boolean isPublicMethod = handlerMethod.hasMethodAnnotation(PublicEndpoint.class);
            boolean isPublicClass = handlerMethod.getBeanType().isAnnotationPresent(PublicEndpoint.class);

            if (isPublicMethod || isPublicClass) {
                populateAuthenticatedUserIfPresent(request);
                return true;
            }
        }

        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || authorizationHeader.trim().isEmpty()) {
            throw new InvalidTokenException("Cabeçalho 'Authorization' ausente ou vazio.");
        }

        if (tokenValidationService == null || !tokenValidationService.isValid(authorizationHeader)) {
            throw new InvalidTokenException("Token de autorização inválido ou expirado.");
        }

        populateAuthenticatedUserIfPresent(request);

        return true;
    }

    private void populateAuthenticatedUserIfPresent(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader == null || authorizationHeader.trim().isEmpty()) {
            return;
        }

        if (jwtService != null && tokenValidationService.isValid(authorizationHeader)) {
            String token = tokenValidationService.extractToken(authorizationHeader);
            Long userId = jwtService.extractUserId(token);
            if (userId != null) {
                request.setAttribute("authenticatedUserId", userId);
            }
            String email = jwtService.extractSubject(token);
            if (email != null) {
                request.setAttribute("authenticatedUserEmail", email);
            }
        }
    }
}
