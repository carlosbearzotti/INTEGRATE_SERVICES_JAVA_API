package com.desafio.integrados.investimento.repository;

import com.desafio.integrados.investimento.model.InvestmentPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvestmentPositionRepository extends JpaRepository<InvestmentPosition, Long> {
    List<InvestmentPosition> findByUserId(Long userId);
}
