package com.desafio.integrados.compliance.service;

import com.desafio.integrados.compliance.dto.LgpdAnonymizeRequest;
import com.desafio.integrados.compliance.dto.LgpdExportResponse;
import com.desafio.integrados.compliance.model.AuditLog;
import com.desafio.integrados.compliance.repository.AuditLogRepository;
import com.desafio.integrados.investimento.repository.InvestmentPositionRepository;
import com.desafio.integrados.usuario.dto.UserProfileResponse;
import com.desafio.integrados.usuario.domain.User;
import com.desafio.integrados.usuario.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ComplianceService {

    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;
    private final InvestmentPositionRepository positionRepository;

    public ComplianceService(UserRepository userRepository, AuditLogRepository auditLogRepository, InvestmentPositionRepository positionRepository) {
        this.userRepository = userRepository;
        this.auditLogRepository = auditLogRepository;
        this.positionRepository = positionRepository;
    }

    public LgpdExportResponse exportData(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + userId));

        UserProfileResponse profile = new UserProfileResponse();
        profile.setId(user.getId());
        profile.setName(user.getName());
        profile.setEmail(user.getEmail());
        profile.setCpf(user.getCpf());
        profile.setIncome(user.getIncome());
        profile.setAge(user.getAge());
        profile.setLatitude(user.getLatitude());
        profile.setLongitude(user.getLongitude());

        LgpdExportResponse response = new LgpdExportResponse();
        response.setPersonalData(profile);
        response.setInvestments(positionRepository.findByUserId(userId));
        response.setAuditTrail(auditLogRepository.findByUserIdOrderByCreatedAtDesc(userId));

        // Registrar log de auditoria
        auditLogRepository.save(new AuditLog(userId, "LGPD_DATA_EXPORT", "User", userId.toString(), "Exportação integral dos dados pessoais"));

        return response;
    }

    public User anonymizeUser(LgpdAnonymizeRequest req) {
        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + req.getUserId()));

        String hash = UUID.randomUUID().toString().substring(0, 8);
        user.setName("TITULAR_ANONIMIZADO_" + hash);
        user.setEmail("esquecido_" + hash + "@lgpd.anonymized");
        user.setCpf("***." + hash.substring(0, 3) + ".***-**");
        user.setLatitude(0.0);
        user.setLongitude(0.0);
        user.setPassword("{ANONIMIZADO_BCRYPT_INUTILIZADO}");

        User saved = userRepository.save(user);

        // Registrar auditoria imutável do direito ao esquecimento
        auditLogRepository.save(new AuditLog(req.getUserId(), "LGPD_ANONYMIZATION", "User", req.getUserId().toString(), req.getReason()));

        return saved;
    }

    public List<AuditLog> listAuditLogs() {
        return auditLogRepository.findByOrderByCreatedAtDesc();
    }
}
