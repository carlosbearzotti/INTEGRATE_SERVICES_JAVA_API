package com.desafio.integrados.agendamento.service;

import com.desafio.integrados.agendamento.dto.ScheduleTransferRequest;
import com.desafio.integrados.agendamento.model.ScheduledTransfer;
import com.desafio.integrados.agendamento.repository.ScheduledTransferRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScheduledTransferService {

    private final ScheduledTransferRepository transferRepository;

    public ScheduledTransferService(ScheduledTransferRepository transferRepository) {
        this.transferRepository = transferRepository;
    }

    public ScheduledTransfer schedule(Long userId, ScheduleTransferRequest req) {
        ScheduledTransfer transfer = new ScheduledTransfer();
        transfer.setUserId(userId != null ? userId : 1L);
        transfer.setRecipientName(req.getRecipientName());
        transfer.setRecipientDocument(req.getRecipientDocument());
        transfer.setAmount(req.getAmount());
        transfer.setTransferType(req.getTransferType() != null ? req.getTransferType() : "PIX");
        transfer.setScheduledFor(req.getScheduledFor() != null ? req.getScheduledFor() : LocalDate.now());
        transfer.setStatus("SCHEDULED");
        transfer.setCreatedAt(LocalDateTime.now());

        return transferRepository.save(transfer);
    }

    public List<ScheduledTransfer> listUserScheduledTransfers(Long userId) {
        if (userId == null) {
            return transferRepository.findByOrderByScheduledForAsc();
        }
        return transferRepository.findByUserIdOrderByScheduledForAsc(userId);
    }

    public void cancelSchedule(Long id) {
        ScheduledTransfer transfer = transferRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado: " + id));
        transfer.setStatus("CANCELLED");
        transferRepository.save(transfer);
    }

    // Cron rodando a cada hora ou periodicamente para executar transferências vencidas
    @Scheduled(fixedDelay = 60000) // A cada 60s para demonstração ativa
    public void processDueTransfers() {
        LocalDate today = LocalDate.now();
        List<ScheduledTransfer> dueTransfers = transferRepository.findByStatusAndScheduledForLessThanEqual("SCHEDULED", today);

        for (ScheduledTransfer transfer : dueTransfers) {
            transfer.setStatus("EXECUTED");
            transfer.setExecutedAt(LocalDateTime.now());
            transferRepository.save(transfer);
        }
    }
}
