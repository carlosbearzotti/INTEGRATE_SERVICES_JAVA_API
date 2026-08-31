package com.desafio.integrados.fraude.service;

import com.desafio.integrados.fraude.dto.FraudEvaluateRequest;
import com.desafio.integrados.fraude.dto.FraudEvaluateResponse;
import com.desafio.integrados.fraude.dto.FraudReviewRequest;
import com.desafio.integrados.fraude.model.FraudAlert;
import com.desafio.integrados.fraude.model.FraudRule;
import com.desafio.integrados.fraude.repository.FraudAlertRepository;
import com.desafio.integrados.fraude.repository.FraudRuleRepository;
import com.desafio.integrados.usuario.domain.User;
import com.desafio.integrados.usuario.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@SuppressWarnings("null")
public class FraudShieldService {

    private final FraudAlertRepository alertRepository;
    private final FraudRuleRepository ruleRepository;
    private final UserRepository userRepository;

    public FraudShieldService(FraudAlertRepository alertRepository, FraudRuleRepository ruleRepository, UserRepository userRepository) {
        this.alertRepository = alertRepository;
        this.ruleRepository = ruleRepository;
        this.userRepository = userRepository;
    }

    public FraudEvaluateResponse evaluateTransaction(FraudEvaluateRequest req) {
        Long userId = req.getUserId() != null ? req.getUserId() : 1L;
        User user = userRepository.findById(userId).orElse(null);

        double userLat = user != null && user.getLatitude() != null ? user.getLatitude() : -23.5505;
        double userLng = user != null && user.getLongitude() != null ? user.getLongitude() : -46.6333;

        double originLat = req.getOriginLat() != null ? req.getOriginLat() : userLat;
        double originLng = req.getOriginLng() != null ? req.getOriginLng() : userLng;

        double distanceKm = calculateHaversineDistance(userLat, userLng, originLat, originLng);
        distanceKm = BigDecimal.valueOf(distanceKm).setScale(2, RoundingMode.HALF_UP).doubleValue();

        FraudRule rule = ruleRepository.findFirstByIsActiveTrue().orElse(new FraudRule());
        double maxDist = rule.getMaxDistanceKm() != null ? rule.getMaxDistanceKm() : 500.0;
        double maxAmount = rule.getMaxAmount() != null ? rule.getMaxAmount() : 50000.0;

        int score = 0;
        List<String> triggers = new ArrayList<>();

        if (distanceKm > maxDist) {
            score += 55;
            triggers.add(String.format("GEOFENCING_BREACH: Distância de %.1f km excede o limite configurado de %.1f km", distanceKm, maxDist));
        } else if (distanceKm > 100.0) {
            score += 25;
            triggers.add(String.format("LOCATION_ANOMALY: Transação a %.1f km da localização habitual", distanceKm));
        }

        if (req.getAmount() >= maxAmount) {
            score += 50;
            triggers.add(String.format("HIGH_AMOUNT_ALERT: Valor R$ %.2f excede o teto diário de segurança", req.getAmount()));
        } else if (req.getAmount() >= 10000.0) {
            score += 20;
            triggers.add(String.format("ELEVATED_VALUE: Transação de alto valor (R$ %.2f)", req.getAmount()));
        }

        score = Math.min(100, score);
        boolean blocked = score >= 50;

        FraudEvaluateResponse res = new FraudEvaluateResponse();
        res.setRiskScore(score);
        res.setApproved(!blocked);
        res.setDecision(blocked ? "BLOCKED_FOR_REVIEW" : "APPROVED");
        res.setDistanceKm(distanceKm);
        res.setTriggeredRules(triggers);

        if (blocked) {
            FraudAlert alert = new FraudAlert();
            alert.setUserId(userId);
            alert.setTransactionAmount(req.getAmount());
            alert.setOriginLat(originLat);
            alert.setOriginLng(originLng);
            alert.setUserLat(userLat);
            alert.setUserLng(userLng);
            alert.setDistanceKm(distanceKm);
            alert.setRiskScore(score);
            alert.setReason(String.join(" | ", triggers));
            alert.setStatus("UNDER_REVIEW");
            alert.setCreatedAt(LocalDateTime.now());

            FraudAlert saved = alertRepository.save(alert);
            res.setAlertId(saved.getId());
        }

        return res;
    }

    public List<FraudAlert> listAlerts(String status) {
        if (status != null && !status.isBlank()) {
            return alertRepository.findByStatusOrderByCreatedAtDesc(status);
        }
        return alertRepository.findByOrderByCreatedAtDesc();
    }

    public FraudAlert reviewAlert(Long alertId, FraudReviewRequest req) {
        FraudAlert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new RuntimeException("Alerta de fraude não encontrado: " + alertId));

        alert.setStatus(req.getDecision());
        alert.setReviewedAt(LocalDateTime.now());
        alert.setReviewedBy(req.getReviewerName() != null ? req.getReviewerName() : "Admin BackOffice");

        return alertRepository.save(alert);
    }

    public FraudRule updateRules(FraudRule newRule) {
        FraudRule rule = ruleRepository.findFirstByIsActiveTrue().orElse(new FraudRule());
        if (newRule.getMaxDistanceKm() != null) rule.setMaxDistanceKm(newRule.getMaxDistanceKm());
        if (newRule.getMaxAmount() != null) rule.setMaxAmount(newRule.getMaxAmount());
        if (newRule.getActive() != null) rule.setActive(newRule.getActive());
        return ruleRepository.save(rule);
    }

    private double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Raio da Terra em km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
