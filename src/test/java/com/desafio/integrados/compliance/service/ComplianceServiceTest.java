package com.desafio.integrados.compliance.service;

import com.desafio.integrados.compliance.dto.LgpdAnonymizeRequest;
import com.desafio.integrados.compliance.dto.LgpdExportResponse;
import com.desafio.integrados.compliance.model.AuditLog;
import com.desafio.integrados.compliance.repository.AuditLogRepository;
import com.desafio.integrados.investimento.repository.InvestmentPositionRepository;
import com.desafio.integrados.usuario.domain.User;
import com.desafio.integrados.usuario.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComplianceServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuditLogRepository auditLogRepository;

    @Mock
    private InvestmentPositionRepository positionRepository;

    @InjectMocks
    private ComplianceService complianceService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("João da Silva");
        user.setEmail("joao@example.com");
        user.setCpf("123.456.789-00");
        user.setIncome(5000.0);
        user.setAge(30);
        user.setLatitude(-23.5505);
        user.setLongitude(-46.6333);
        user.setPassword("senha123");
    }

    @Test
    void shouldExportDataSuccessfully() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(positionRepository.findByUserId(1L)).thenReturn(new ArrayList<>());
        when(auditLogRepository.findByUserIdOrderByCreatedAtDesc(1L)).thenReturn(new ArrayList<>());

        LgpdExportResponse response = complianceService.exportData(1L);

        assertNotNull(response);
        assertNotNull(response.getPersonalData());
        assertEquals("João da Silva", response.getPersonalData().getName());
        assertEquals("123.456.789-00", response.getPersonalData().getCpf());
        
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    void shouldThrowExceptionWhenExportingDataForUnknownUser() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            complianceService.exportData(99L);
        });

        assertEquals("Usuário não encontrado: 99", exception.getMessage());
    }

    @Test
    void shouldAnonymizeUserSuccessfully() {
        LgpdAnonymizeRequest req = new LgpdAnonymizeRequest();
        req.setUserId(1L);
        req.setReason("Solicitação do titular");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        User anonymized = complianceService.anonymizeUser(req);

        assertNotNull(anonymized);
        assertTrue(anonymized.getName().startsWith("TITULAR_ANONIMIZADO_"));
        assertTrue(anonymized.getEmail().endsWith("@lgpd.anonymized"));
        assertTrue(anonymized.getCpf().startsWith("***."));
        assertEquals(0.0, anonymized.getLatitude());
        assertEquals(0.0, anonymized.getLongitude());
        assertEquals("{ANONIMIZADO_BCRYPT_INUTILIZADO}", anonymized.getPassword());

        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }
}
