package com.desafio.integrados.agendamento.repository;

import com.desafio.integrados.agendamento.model.ScheduledTransfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ScheduledTransferRepository extends JpaRepository<ScheduledTransfer, Long> {
    List<ScheduledTransfer> findByUserIdOrderByScheduledForAsc(Long userId);
    List<ScheduledTransfer> findByStatusAndScheduledForLessThanEqual(String status, LocalDate date);
    List<ScheduledTransfer> findByOrderByScheduledForAsc();
}
