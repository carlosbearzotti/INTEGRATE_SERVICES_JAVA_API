package com.desafio.integrados.usuario.service;

import com.desafio.integrados.autenticacao.service.JwtService;
import com.desafio.integrados.senhasegura.dto.PasswordValidationRequest;
import com.desafio.integrados.senhasegura.service.PasswordValidationService;
import com.desafio.integrados.usuario.domain.User;
import com.desafio.integrados.usuario.dto.*;
import com.desafio.integrados.usuario.exception.AccountNotVerifiedException;
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
    private final Map<String, TokenEntry> resetTokens = new ConcurrentHashMap<>();

    // Cache em memória para tokens temporários de ativação de conta (Validade: 15 minutos)
    private final Map<String, ActivationEntry> activationTokens = new ConcurrentHashMap<>();

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(3))
            .build();

    private static class TokenEntry {
        private final String code;
        private final long expiresAt;

        public TokenEntry(String code, long expiresAt) {
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

    private static class ActivationEntry {
        private final String code;
        private final long expiresAt;
        private final String cardPin;

        public ActivationEntry(String code, long expiresAt, String cardPin) {
            this.code = code;
            this.expiresAt = expiresAt;
            this.cardPin = cardPin;
        }

        public boolean isValid(String providedCode) {
            if (providedCode == null || !this.code.trim().equalsIgnoreCase(providedCode.trim())) {
                return false;
            }
            return System.currentTimeMillis() <= this.expiresAt;
        }

        public String getCardPin() {
            return cardPin;
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

        // Cria o usuário com status emailVerified = false (pendente de ativação)
        User user = new User(
                request.getName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()), // Salva a senha com hash BCrypt
                request.getCpf(),
                request.getIncome(),
                request.getAge(),
                request.getLatitude(),
                request.getLongitude(),
                "ROLE_CUSTOMER",
                false // Obrigatório confirmar por e-mail
        );

        User saved = userRepository.save(user);

        // Gera código de ativação de 6 dígitos único
        int code = 100000 + new Random().nextInt(900000);
        String activationCode = String.valueOf(code);
        long expiresAt = System.currentTimeMillis() + (15 * 60 * 1000);

        String cardPin = (request.getCardPin() != null && !request.getCardPin().isBlank())
                ? request.getCardPin().trim()
                : String.format("%04d", (int)(Math.random() * 9000) + 1000);

        String emailKey = saved.getEmail().toLowerCase().trim();
        activationTokens.put(emailKey, new ActivationEntry(activationCode, expiresAt, cardPin));

        log.info("Código de ativação gerado para {}: {}", emailKey, activationCode);

        // Despacha e-mail de ativação obrigatório para o consumerNotification (Porta 3002)
        dispatchAccountActivationNotification(saved.getEmail(), saved.getName(), activationCode);

        return new UserRegistrationResponse(
                saved.getId(),
                saved.getName(),
                saved.getEmail(),
                saved.getCpf(),
                saved.getIncome(),
                saved.getAge(),
                saved.getLatitude(),
                saved.getLongitude(),
                cardPin,
                "PENDING_ACTIVATION",
                "Conta criada com sucesso! Digite o código de 6 dígitos enviado para seu e-mail no Notify Hub para liberar seu acesso."
        );
    }

    public VerifyAccountResponse verifyAccount(VerifyAccountRequest request) {
        String emailKey = request.getEmail().toLowerCase().trim();
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("Nenhuma conta encontrada com o e-mail: " + request.getEmail()));

        if (user.isEmailVerified()) {
            return new VerifyAccountResponse(true, "Sua conta já se encontra ativada!", user.getEmail(), null);
        }

        ActivationEntry entry = activationTokens.get(emailKey);
        if (entry == null || !entry.isValid(request.getCode())) {
            log.warn("Tentativa de ativação de conta com código inválido para {}", emailKey);
            throw new InvalidCredentialsException("Código de ativação inválido ou expirado. Verifique seu e-mail no Notify Hub.");
        }

        // Ativa a conta
        user.setEmailVerified(true);
        userRepository.save(user);

        String cardPin = entry.getCardPin();
        activationTokens.remove(emailKey);

        log.info("Conta ativada com sucesso para {}", emailKey);

        // Agora que o e-mail foi validado, emite o cartão e envia o e-mail de boas-vindas
        dispatchCardIssuedNotification(user.getEmail(), user.getName());

        return new VerifyAccountResponse(
                true,
                "Conta ativada com sucesso! Seu acesso ao LãoBank está liberado.",
                user.getEmail(),
                cardPin
        );
    }

    public VerifyAccountResponse resendActivationCode(String email) {
        String emailKey = email.toLowerCase().trim();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Nenhuma conta encontrada com o e-mail: " + email));

        if (user.isEmailVerified()) {
            return new VerifyAccountResponse(true, "Sua conta já está ativada. Você pode fazer login normalmente.", user.getEmail(), null);
        }

        int code = 100000 + new Random().nextInt(900000);
        String activationCode = String.valueOf(code);
        long expiresAt = System.currentTimeMillis() + (15 * 60 * 1000);

        ActivationEntry currentEntry = activationTokens.get(emailKey);
        String cardPin = currentEntry != null ? currentEntry.getCardPin() : String.format("%04d", (int)(Math.random() * 9000) + 1000);

        activationTokens.put(emailKey, new ActivationEntry(activationCode, expiresAt, cardPin));

        log.info("Novo código de ativação reenviado para {}: {}", emailKey, activationCode);
        dispatchAccountActivationNotification(user.getEmail(), user.getName(), activationCode);

        return new VerifyAccountResponse(
                false,
                "Novo código de 6 dígitos enviado para seu e-mail! Verifique sua caixa de entrada no Notify Hub (Porta 3002).",
                user.getEmail(),
                null
        );
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("E-mail ou senha inválidos."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("E-mail ou senha inválidos.");
        }

        // Bloqueia acesso caso a conta não tenha sido ativada via código de e-mail (exceto admin)
        if (!user.isEmailVerified() && !"ROLE_ADMIN".equalsIgnoreCase(user.getRole())) {
            // Reenvia automaticamente o código de ativação para conveniência
            resendActivationCode(user.getEmail());
            throw new AccountNotVerifiedException("Sua conta ainda não foi ativada. Enviamos um novo código de verificação para seu e-mail no Notify Hub.");
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
                .orElseThrow(() -> new InvalidCredentialsException("Credenciais de colaborador inválidas."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Credenciais de colaborador inválidas.");
        }

        if (!"ROLE_ADMIN".equalsIgnoreCase(user.getRole()) && !"ROLE_EMPLOYEE".equalsIgnoreCase(user.getRole())) {
            throw new InvalidCredentialsException("Acesso restrito apenas a colaboradores e administradores.");
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

    public UserProfileResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado com o ID: " + userId));

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
        resetTokens.put(emailKey, new TokenEntry(resetCode, expiresAt));

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
        TokenEntry entry = resetTokens.get(emailKey);
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

    private void dispatchAccountActivationNotification(String email, String name, String activationCode) {
        try {
            String jsonPayload = String.format(
                    "{\"to\":\"%s\",\"token\":\"%s\",\"template\":\"account_activation\",\"name\":\"%s\",\"subject\":\"🔒 Confirmação de Abertura de Conta LãoBank - Código: %s\"}",
                    email, activationCode, name != null ? name : "Cliente LãoBank", activationCode
            );

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:3002/api/notify"))
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(3))
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload, StandardCharsets.UTF_8))
                    .build();

            httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.discarding())
                    .thenAccept(res -> log.info("Notificação de ativação de conta enviada ao consumerNotification (3002): Status {}", res.statusCode()))
                    .exceptionally(ex -> {
                        log.warn("consumerNotification (Porta 3002) indisponível para ativação de conta: {}", ex.getMessage());
                        return null;
                    });
        } catch (Exception e) {
            log.warn("Falha ao preparar requisição de ativação de conta: {}", e.getMessage());
        }
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

    private void dispatchCardIssuedNotification(String email, String name) {
        try {
            String jsonPayload = String.format(
                    "{\"to\":\"%s\",\"name\":\"%s\",\"template\":\"card_issued\",\"last4\":\"8824\",\"deliveryDays\":7,\"subject\":\"💳 Seu Cartão LãoBank foi emitido! Físico em até 7 dias e Virtual liberado\"}",
                    email, name != null ? name : "Cliente LãoBank"
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
