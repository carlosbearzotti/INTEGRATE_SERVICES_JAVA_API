package com.desafio.integrados.emprestimo.repository;

import com.desafio.integrados.emprestimo.domain.LoanContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanContractRepository extends JpaRepository<LoanContract, Long> {
    List<LoanContract> findByUserIdOrderByContractedAtDesc(Long userId);
}
