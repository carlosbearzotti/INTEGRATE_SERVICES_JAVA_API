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
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordValidationService passwordValidationService;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository,
                       PasswordValidationService passwordValidationService,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordValidationService = passwordValidationService;
        this.jwtService = jwtService;
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
                request.getPassword(), // Em produção usaríamos BCrypt
                request.getCpf(),
                request.getIncome(),
                request.getAge(),
                request.getLatitude(),
                request.getLongitude()
        );

        User saved = userRepository.save(user);

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

        if (!user.getPassword().equals(request.getPassword())) {
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

        if (!user.getPassword().equals(request.getPassword())) {
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

        // Gera código de segurança de 6 dígitos
        int code = 100000 + new java.util.Random().nextInt(900000);
        String resetCode = String.valueOf(code);

        return new ForgotPasswordResponse(
                "Instruções e código de recuperação enviados com sucesso!",
                user.getEmail(),
                resetCode,
                true
        );
    }

    public ResetPasswordResponse resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("Nenhuma conta encontrada com o e-mail: " + request.getEmail()));

        // Validar conformidade com as 5 regras do módulo SenhaSegura
        passwordValidationService.validate(new PasswordValidationRequest(request.getNewPassword()));

        // Atualizar senha no repositório
        user.setPassword(request.getNewPassword());
        userRepository.save(user);

        return new ResetPasswordResponse(true, "Senha de acesso ao LãoBank atualizada com sucesso!");
    }
}
