package com.desafio.integrados.fraude.repository;

import com.desafio.integrados.fraude.model.FraudRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FraudRuleRepository extends JpaRepository<FraudRule, Long> {
    Optional<FraudRule> findFirstByIsActiveTrue();
}
