package com.desafio.integrados.investimento.service;

import com.desafio.integrados.investimento.dto.InvestmentApplyRequest;
import com.desafio.integrados.investimento.dto.InvestmentSimulationRequest;
import com.desafio.integrados.investimento.dto.InvestmentSimulationResponse;
import com.desafio.integrados.investimento.model.InvestmentPosition;
import com.desafio.integrados.investimento.model.InvestmentProduct;
import com.desafio.integrados.investimento.repository.InvestmentPositionRepository;
import com.desafio.integrados.investimento.repository.InvestmentProductRepository;
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
class InvestmentServiceTest {

    @Mock
    private InvestmentProductRepository productRepository;

    @Mock
    private InvestmentPositionRepository positionRepository;

    @InjectMocks
    private InvestmentService investmentService;

    private InvestmentProduct product;

    @BeforeEach
    void setUp() {
        product = new InvestmentProduct();
        product.setId(1L);
        product.setName("CDB Prefixado");
        product.setType("FIXED_INCOME");
        product.setRatePercent(110.0);
        product.setMinAmount(500.0);
        product.setIrExempt(false);
        product.setActive(true);
    }

    @Test
    void shouldSimulateInvestmentSuccessfully() {
        InvestmentSimulationRequest request = new InvestmentSimulationRequest();
        request.setInitialAmount(1000.0);
        request.setMonthlyContribution(100.0);
        request.setMonths(12);
        request.setCdiRateAnnual(10.75);
        request.setProductRatePercent(110.0);
        request.setIrExempt(false);

        InvestmentSimulationResponse response = investmentService.simulate(request);

        assertNotNull(response);
        assertEquals(2200.0, response.getTotalInvested());
        assertTrue(response.getGrossYield() > 0);
        assertTrue(response.getNetYield() > 0);
        assertEquals(20.0, response.getIrTaxRate()); // 12 months = 360 days -> 20.0%
        assertTrue(response.getNetTotal() > 2200.0);
        assertEquals(13, response.getEvolution().size()); // Mês 0 até 12
    }

    @Test
    void shouldApplyForInvestmentSuccessfully() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(positionRepository.save(any(InvestmentPosition.class))).thenAnswer(i -> {
            InvestmentPosition pos = i.getArgument(0);
            pos.setId(100L);
            return pos;
        });

        InvestmentApplyRequest request = new InvestmentApplyRequest();
        request.setProductId(1L);
        request.setAmount(1000.0);

        InvestmentPosition position = investmentService.apply(1L, request);

        assertNotNull(position);
        assertEquals(1L, position.getUserId());
        assertEquals("CDB Prefixado", position.getProductName());
        assertEquals(1000.0, position.getPrincipalAmount());
        assertEquals(1000.0, position.getCurrentAmount());
        assertEquals("ACTIVE", position.getStatus());
        
        verify(positionRepository, times(1)).save(any(InvestmentPosition.class));
    }

    @Test
    void shouldThrowExceptionWhenApplyingBelowMinimumAmount() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product)); // Min = 500.0

        InvestmentApplyRequest request = new InvestmentApplyRequest();
        request.setProductId(1L);
        request.setAmount(400.0); // Abaixo do mínimo

        Exception exception = assertThrows(RuntimeException.class, () -> {
            investmentService.apply(1L, request);
        });

        assertEquals("O valor mínimo para aplicação neste produto é de R$ 500.0", exception.getMessage());
        verify(positionRepository, never()).save(any());
    }
}
