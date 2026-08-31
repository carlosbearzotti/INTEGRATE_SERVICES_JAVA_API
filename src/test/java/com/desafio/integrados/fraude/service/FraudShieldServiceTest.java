package com.desafio.integrados.fraude.service;

import com.desafio.integrados.fraude.dto.FraudEvaluateRequest;
import com.desafio.integrados.fraude.dto.FraudEvaluateResponse;
import com.desafio.integrados.fraude.model.FraudAlert;
import com.desafio.integrados.fraude.model.FraudRule;
import com.desafio.integrados.fraude.repository.FraudAlertRepository;
import com.desafio.integrados.fraude.repository.FraudRuleRepository;
import com.desafio.integrados.usuario.domain.User;
import com.desafio.integrados.usuario.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FraudShieldServiceTest {

    @Mock
    private FraudAlertRepository alertRepository;

    @Mock
    private FraudRuleRepository ruleRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FraudShieldService fraudShieldService;

    private User user;
    private FraudRule rule;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setLatitude(-23.5505);
        user.setLongitude(-46.6333);

        rule = new FraudRule();
        rule.setMaxDistanceKm(500.0);
        rule.setMaxAmount(50000.0);
        rule.setActive(true);
    }

    @Test
    void shouldApproveNormalTransaction() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(ruleRepository.findFirstByIsActiveTrue()).thenReturn(Optional.of(rule));

        FraudEvaluateRequest request = new FraudEvaluateRequest();
        request.setUserId(1L);
        request.setAmount(5000.0);
        request.setOriginLat(-23.5505); // Mesma localização
        request.setOriginLng(-46.6333);

        FraudEvaluateResponse response = fraudShieldService.evaluateTransaction(request);

        assertTrue(response.isApproved());
        assertEquals("APPROVED", response.getDecision());
        assertEquals(0, response.getRiskScore());
        verify(alertRepository, never()).save(any());
    }

    @Test
    void shouldBlockTransactionExceedingDistance() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(ruleRepository.findFirstByIsActiveTrue()).thenReturn(Optional.of(rule));
        when(alertRepository.save(any(FraudAlert.class))).thenAnswer(i -> {
            FraudAlert alert = i.getArgument(0);
            alert.setId(99L);
            return alert;
        });

        FraudEvaluateRequest request = new FraudEvaluateRequest();
        request.setUserId(1L);
        request.setAmount(100.0);
        request.setOriginLat(40.7128); // Nova York, muito longe
        request.setOriginLng(-74.0060);

        FraudEvaluateResponse response = fraudShieldService.evaluateTransaction(request);

        assertFalse(response.isApproved());
        assertEquals("BLOCKED_FOR_REVIEW", response.getDecision());
        assertTrue(response.getRiskScore() >= 50);
        assertNotNull(response.getAlertId());
        verify(alertRepository, times(1)).save(any(FraudAlert.class));
    }

    @Test
    void shouldBlockTransactionExceedingAmount() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(ruleRepository.findFirstByIsActiveTrue()).thenReturn(Optional.of(rule));
        when(alertRepository.save(any(FraudAlert.class))).thenAnswer(i -> {
            FraudAlert alert = i.getArgument(0);
            alert.setId(100L);
            return alert;
        });

        FraudEvaluateRequest request = new FraudEvaluateRequest();
        request.setUserId(1L);
        request.setAmount(100000.0); // Maior que 50000
        request.setOriginLat(-23.5505);
        request.setOriginLng(-46.6333);

        FraudEvaluateResponse response = fraudShieldService.evaluateTransaction(request);

        assertFalse(response.isApproved());
        assertEquals("BLOCKED_FOR_REVIEW", response.getDecision());
        assertTrue(response.getRiskScore() >= 50);
        verify(alertRepository, times(1)).save(any(FraudAlert.class));
    }
}
