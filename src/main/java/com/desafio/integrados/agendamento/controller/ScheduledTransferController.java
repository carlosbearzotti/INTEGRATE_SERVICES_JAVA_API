package com.desafio.integrados.agendamento.controller;

import com.desafio.integrados.agendamento.dto.ScheduleTransferRequest;
import com.desafio.integrados.agendamento.model.ScheduledTransfer;
import com.desafio.integrados.agendamento.service.ScheduledTransferService;
import com.desafio.integrados.autenticacao.annotation.PublicEndpoint;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transfers")
public class ScheduledTransferController {

    private final ScheduledTransferService transferService;

    public ScheduledTransferController(ScheduledTransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping("/schedule")
    @PublicEndpoint
    public ResponseEntity<ScheduledTransfer> schedule(@Valid @RequestBody ScheduleTransferRequest request, HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("authenticatedUserId");
        if (userId == null) userId = 1L;
        ScheduledTransfer transfer = transferService.schedule(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(transfer);
    }

    @GetMapping("/scheduled")
    @PublicEndpoint
    public ResponseEntity<List<ScheduledTransfer>> listScheduled(HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("authenticatedUserId");
        return ResponseEntity.ok(transferService.listUserScheduledTransfers(userId));
    }

    @DeleteMapping("/schedule/{id}")
    @PublicEndpoint
    public ResponseEntity<Void> cancelSchedule(@PathVariable Long id) {
        transferService.cancelSchedule(id);
        return ResponseEntity.noContent().build();
    }
}
