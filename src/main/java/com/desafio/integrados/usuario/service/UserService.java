package com.desafio.integrados.usuario.service;

import com.desafio.integrados.autenticacao.service.JwtService;
import com.desafio.integrados.senhasegura.dto.PasswordValidationRequest;
import com.desafio.integrados.senhasegura.service.PasswordValidationService;
import com.desafio.integrados.usuario.domain.User;
import com.desafio.integrados.usuario.dto.*;
import com.desafio.integrados.usuario.exception.InvalidCredentialsException;
import com.desafio.integrados.usuario.exception.UserAlreadyExistsException;
import com.desafio.integrados.usuario.exception.UserNotFoundException;
import com.desafio.integrados.usuario.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordValidationService passwordValidationService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    // Cache em memória para tokens temporários de recuperação (Validade: 15 minutos)
    private final Map<String, ResetTokenEntry> resetTokens = new ConcurrentHashMap<>();

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(3))
            .build();

    private static class ResetTokenEntry {
        private final String code;
        private final long expiresAt;

        public ResetTokenEntry(String code, long expiresAt) {
            this.code = code;
            this.expiresAt = expiresAt;
        }

        public boolean isValid(String providedCode) {
            if (providedCode == null || !this.code.trim().equalsIgnoreCase(providedCode.trim())) {
                return false;
            }
            return System.currentTimeMillis() <= this.expiresAt;
        }
    }

    public UserService(UserRepository userRepository,
                       PasswordValidationService passwordValidationService,
                       JwtService jwtService,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordValidationService = passwordValidationService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    public UserRegistrationResponse register(UserRegistrationRequest request) {
        // Validação da força da senha usando o módulo SenhaSegura
        passwordValidationService.validate(new PasswordValidationRequest(request.getPassword()));

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Já existe um usuário cadastrado com este e-mail.");
        }

        if (userRepository.findByCpf(request.getCpf()).isPresent()) {
            throw new UserAlreadyExistsException("Já existe um usuário cadastrado com este CPF.");
        }

        User user = new User(
                request.getName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()), // Salva a senha com hash BCrypt
                request.getCpf(),
                request.getIncome(),
                request.getAge(),
                request.getLatitude(),
                request.getLongitude()
        );

        User saved = userRepository.save(user);

        // Despacha e-mail de boas-vindas com emissão de cartão físico (7 dias), virtual disponível e senha provisória
        String cardPin = String.format("%04d", (int)(Math.random() * 9000) + 1000);
        dispatchCardIssuedNotification(saved.getEmail(), saved.getName(), cardPin);

        return new UserRegistrationResponse(
                saved.getId(),
                saved.getName(),
                saved.getEmail(),
                saved.getCpf(),
                saved.getIncome(),
                saved.getAge(),
                saved.getLatitude(),
                saved.getLongitude()
        );
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("E-mail ou senha inválidos."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("E-mail ou senha inválidos.");
        }

        String token = jwtService.generateToken(user);

        return new LoginResponse(
                token,
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }

    public LoginResponse employeeLogin(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Credenciais corporativas inválidas."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Credenciais corporativas inválidas.");
        }

        // Validação de acesso restrito a colaboradores e administradores
        if (!"ROLE_ADMIN".equals(user.getRole()) && !"ROLE_EMPLOYEE".equals(user.getRole())) {
            throw new InvalidCredentialsException("Acesso negado: apenas colaboradores e administradores do LãoBank têm permissão de acesso ao Portal BackOffice.");
        }

        String token = jwtService.generateToken(user);

        return new LoginResponse(
                token,
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }

    public User findById(@org.springframework.lang.NonNull Long id) {
        return userRepository.findById(java.util.Objects.requireNonNull(id))
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado com id: " + id));
    }

    public UserProfileResponse getProfile(@org.springframework.lang.NonNull Long userId) {
        User user = findById(java.util.Objects.requireNonNull(userId));
        return new UserProfileResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCpf(),
                user.getIncome(),
                user.getAge(),
                user.getLatitude(),
                user.getLongitude(),
                user.getRole()
        );
    }

    public ForgotPasswordResponse forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("Nenhuma conta encontrada com o e-mail: " + request.getEmail()));

        // Gera código de segurança de 6 dígitos único
        int code = 100000 + new Random().nextInt(900000);
        String resetCode = String.valueOf(code);

        // Salva no cache com expiração em 15 minutos
        long expiresAt = System.currentTimeMillis() + (15 * 60 * 1000);
        String emailKey = user.getEmail().toLowerCase().trim();
        resetTokens.put(emailKey, new ResetTokenEntry(resetCode, expiresAt));

        log.info("Token de recuperação gerado para {}: {}", emailKey, resetCode);

        // Despacha para o middleware consumerNotification (Porta 3002)
        dispatchNotification(user.getEmail(), user.getName(), resetCode);

        return new ForgotPasswordResponse(
                "Instruções e código de recuperação enviados com sucesso para seu e-mail!",
                user.getEmail(),
                null, // Ocultado por segurança - obriga a consultar a caixa de entrada no Notify Hub (Porta 3002)
                true
        );
    }

    public ResetPasswordResponse resetPassword(ResetPasswordRequest request) {
        String emailKey = request.getEmail().toLowerCase().trim();
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("Nenhuma conta encontrada com o e-mail: " + request.getEmail()));

        // Validação estrita do código de recuperação
        ResetTokenEntry entry = resetTokens.get(emailKey);
        if (entry == null || !entry.isValid(request.getResetCode())) {
            log.warn("Tentativa de redefinição de senha com código inválido para {}", emailKey);
            throw new InvalidCredentialsException("Código de recuperação inválido ou expirado. Verifique seu e-mail no Notify Hub.");
        }

        // Validar conformidade com as 5 regras do módulo SenhaSegura
        passwordValidationService.validate(new PasswordValidationRequest(request.getNewPassword()));

        // Atualizar senha no repositório com hash BCrypt
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Invalida o token utilizado
        resetTokens.remove(emailKey);
        log.info("Senha redefinida com sucesso para {}", emailKey);

        return new ResetPasswordResponse(true, "Senha de acesso ao LãoBank atualizada com sucesso!");
    }

    private void dispatchNotification(String email, String name, String resetCode) {
        try {
            String jsonPayload = String.format(
                    "{\"to\":\"%s\",\"token\":\"%s\",\"subject\":\"Código de Recuperação de Senha - LãoBank Digital\",\"name\":\"%s\",\"template\":\"password_reset\"}",
                    email, resetCode, name != null ? name : "Cliente LãoBank"
            );

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:3002/api/notify"))
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(3))
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload, StandardCharsets.UTF_8))
                    .build();

            httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.discarding())
                    .thenAccept(res -> log.info("Notificação enviada com sucesso para o consumerNotification (3002): Status {}", res.statusCode()))
                    .exceptionally(ex -> {
                        log.warn("consumerNotification (Porta 3002) indisponível: {}", ex.getMessage());
                        return null;
                    });
        } catch (Exception e) {
            log.warn("Falha ao preparar requisição de notificação: {}", e.getMessage());
        }
    }

    private void dispatchCardIssuedNotification(String email, String name, String cardPin) {
        try {
            String jsonPayload = String.format(
                    "{\"to\":\"%s\",\"name\":\"%s\",\"template\":\"card_issued\",\"last4\":\"8824\",\"deliveryDays\":7,\"pin\":\"%s\",\"subject\":\"💳 Seu Cartão LãoBank foi emitido! Físico em até 7 dias, Virtual liberado e Senha Inicial\"}",
                    email, name != null ? name : "Cliente LãoBank", cardPin
            );

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:3002/api/notify"))
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(3))
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload, StandardCharsets.UTF_8))
                    .build();

            httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.discarding())
                    .thenAccept(res -> log.info("Notificação de cartão emitido enviada com sucesso ao consumerNotification (3002): Status {}", res.statusCode()))
                    .exceptionally(ex -> {
                        log.warn("consumerNotification (Porta 3002) indisponível para aviso de cartão: {}", ex.getMessage());
                        return null;
                    });
        } catch (Exception e) {
            log.warn("Falha ao preparar notificação de cartão emitido: {}", e.getMessage());
        }
    }
}
