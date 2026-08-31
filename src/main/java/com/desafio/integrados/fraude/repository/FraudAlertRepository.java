package com.desafio.integrados.fraude.repository;

import com.desafio.integrados.fraude.model.FraudAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FraudAlertRepository extends JpaRepository<FraudAlert, Long> {
    List<FraudAlert> findByOrderByCreatedAtDesc();
    List<FraudAlert> findByStatusOrderByCreatedAtDesc(String status);
}
