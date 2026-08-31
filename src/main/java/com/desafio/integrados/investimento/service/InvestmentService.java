package com.desafio.integrados.investimento.service;

import com.desafio.integrados.investimento.dto.InvestmentApplyRequest;
import com.desafio.integrados.investimento.dto.InvestmentSimulationRequest;
import com.desafio.integrados.investimento.dto.InvestmentSimulationResponse;
import com.desafio.integrados.investimento.model.InvestmentPosition;
import com.desafio.integrados.investimento.model.InvestmentProduct;
import com.desafio.integrados.investimento.repository.InvestmentPositionRepository;
import com.desafio.integrados.investimento.repository.InvestmentProductRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class InvestmentService {

    private final InvestmentProductRepository productRepository;
    private final InvestmentPositionRepository positionRepository;

    public InvestmentService(InvestmentProductRepository productRepository, InvestmentPositionRepository positionRepository) {
        this.productRepository = productRepository;
        this.positionRepository = positionRepository;
    }

    public List<InvestmentProduct> listAvailableProducts() {
        return productRepository.findByActiveTrue();
    }

    public InvestmentSimulationResponse simulate(InvestmentSimulationRequest req) {
        double initial = req.getInitialAmount();
        double monthly = req.getMonthlyContribution() != null ? req.getMonthlyContribution() : 0.0;
        int months = req.getMonths();
        double cdiAnnual = req.getCdiRateAnnual() != null ? req.getCdiRateAnnual() : 10.75;
        double productRate = req.getProductRatePercent() != null ? req.getProductRatePercent() : 120.0;
        boolean irExempt = Boolean.TRUE.equals(req.getIrExempt());

        // Taxa anual efetiva do produto = (CDI * (productRate / 100)) / 100
        double effectiveAnnualRate = (cdiAnnual * (productRate / 100.0)) / 100.0;
        // Taxa mensal equivalente = (1 + i)^(1/12) - 1
        double monthlyRate = Math.pow(1.0 + effectiveAnnualRate, 1.0 / 12.0) - 1.0;

        // Poupança benchmark: ~ 0.5% ao mês (6.17% a.a.)
        double poupancaMonthlyRate = Math.pow(1.0 + 0.0617, 1.0 / 12.0) - 1.0;

        double currentGross = initial;
        double currentInvested = initial;
        double currentPoupanca = initial;

        List<InvestmentSimulationResponse.MonthlyEvolution> evolution = new ArrayList<>();
        evolution.add(new InvestmentSimulationResponse.MonthlyEvolution(0, round(currentInvested), round(currentGross), round(currentGross)));

        for (int m = 1; m <= months; m++) {
            currentGross = (currentGross * (1.0 + monthlyRate)) + monthly;
            currentPoupanca = (currentPoupanca * (1.0 + poupancaMonthlyRate)) + monthly;
            currentInvested += monthly;

            double tempYield = Math.max(0, currentGross - currentInvested);
            double tempIrRate = getIrRateForMonths(m, irExempt);
            double tempIr = tempYield * tempIrRate;
            double tempNet = currentGross - tempIr;

            evolution.add(new InvestmentSimulationResponse.MonthlyEvolution(m, round(currentInvested), round(currentGross), round(tempNet)));
        }

        double totalGrossYield = Math.max(0, currentGross - currentInvested);
        double finalIrRate = getIrRateForMonths(months, irExempt);
        double irTaxAmount = totalGrossYield * finalIrRate;
        double netTotal = currentGross - irTaxAmount;
        double netYield = netTotal - currentInvested;

        InvestmentSimulationResponse res = new InvestmentSimulationResponse();
        res.setTotalInvested(round(currentInvested));
        res.setGrossYield(round(totalGrossYield));
        res.setGrossTotal(round(currentGross));
        res.setIrTaxAmount(round(irTaxAmount));
        res.setIrTaxRate(round(finalIrRate * 100.0));
        res.setNetTotal(round(netTotal));
        res.setNetYield(round(netYield));
        res.setBenchmarkPoupanca(round(currentPoupanca));
        res.setProfitOverPoupanca(round(Math.max(0, netTotal - currentPoupanca)));
        res.setEvolution(evolution);

        return res;
    }

    public InvestmentPosition apply(Long userId, InvestmentApplyRequest req) {
        InvestmentProduct product = productRepository.findById(req.getProductId())
                .orElseThrow(() -> new RuntimeException("Produto de investimento não encontrado: " + req.getProductId()));

        if (req.getAmount() < product.getMinAmount()) {
            throw new RuntimeException("O valor mínimo para aplicação neste produto é de R$ " + product.getMinAmount());
        }

        InvestmentPosition pos = new InvestmentPosition();
        pos.setUserId(userId != null ? userId : 1L);
        pos.setProductName(product.getName());
        pos.setProductType(product.getType());
        pos.setPrincipalAmount(req.getAmount());
        pos.setCurrentAmount(req.getAmount());
        pos.setRatePercent(product.getRatePercent());
        pos.setIrExempt(product.getIrExempt());
        pos.setAppliedAt(LocalDateTime.now());
        pos.setMaturityDate(LocalDateTime.now().plusMonths(12));
        pos.setStatus("ACTIVE");

        return positionRepository.save(pos);
    }

    public List<InvestmentPosition> listUserPositions(Long userId) {
        return positionRepository.findByUserId(userId != null ? userId : 1L);
    }

    private double getIrRateForMonths(int months, boolean irExempt) {
        if (irExempt) return 0.0;
        int days = months * 30;
        if (days <= 180) return 0.225; // 22.5%
        if (days <= 360) return 0.200; // 20.0%
        if (days <= 720) return 0.175; // 17.5%
        return 0.150; // 15.0%
    }

    private double round(double val) {
        return BigDecimal.valueOf(val).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
