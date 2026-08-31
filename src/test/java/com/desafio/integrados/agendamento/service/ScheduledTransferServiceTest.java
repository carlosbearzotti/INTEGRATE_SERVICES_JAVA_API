package com.desafio.integrados.agendamento.service;

import com.desafio.integrados.agendamento.dto.ScheduleTransferRequest;
import com.desafio.integrados.agendamento.model.ScheduledTransfer;
import com.desafio.integrados.agendamento.repository.ScheduledTransferRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduledTransferServiceTest {

    @Mock
    private ScheduledTransferRepository transferRepository;

    @InjectMocks
    private ScheduledTransferService scheduledTransferService;

    @Test
    void shouldScheduleTransferSuccessfully() {
        when(transferRepository.save(any(ScheduledTransfer.class))).thenAnswer(i -> {
            ScheduledTransfer transfer = i.getArgument(0);
            transfer.setId(10L);
            return transfer;
        });

        ScheduleTransferRequest request = new ScheduleTransferRequest();
        request.setRecipientName("João");
        request.setRecipientDocument("12345678909");
        request.setAmount(500.0);
        request.setTransferType("PIX");
        request.setScheduledFor(LocalDate.now().plusDays(2));

        ScheduledTransfer transfer = scheduledTransferService.schedule(1L, request);

        assertNotNull(transfer);
        assertEquals(1L, transfer.getUserId());
        assertEquals("SCHEDULED", transfer.getStatus());
        assertEquals(500.0, transfer.getAmount());
        assertEquals("PIX", transfer.getTransferType());

        verify(transferRepository, times(1)).save(any(ScheduledTransfer.class));
    }

    @Test
    void shouldCancelScheduleSuccessfully() {
        ScheduledTransfer transfer = new ScheduledTransfer();
        transfer.setId(10L);
        transfer.setStatus("SCHEDULED");

        when(transferRepository.findById(10L)).thenReturn(Optional.of(transfer));
        when(transferRepository.save(any(ScheduledTransfer.class))).thenAnswer(i -> i.getArgument(0));

        scheduledTransferService.cancelSchedule(10L);

        assertEquals("CANCELLED", transfer.getStatus());
        verify(transferRepository, times(1)).save(transfer);
    }

    @Test
    void shouldProcessDueTransfersSuccessfully() {
        ScheduledTransfer transfer1 = new ScheduledTransfer();
        transfer1.setId(1L);
        transfer1.setStatus("SCHEDULED");
        transfer1.setScheduledFor(LocalDate.now().minusDays(1)); // Vencida

        ScheduledTransfer transfer2 = new ScheduledTransfer();
        transfer2.setId(2L);
        transfer2.setStatus("SCHEDULED");
        transfer2.setScheduledFor(LocalDate.now()); // Vencendo hoje

        List<ScheduledTransfer> dueTransfers = new ArrayList<>();
        dueTransfers.add(transfer1);
        dueTransfers.add(transfer2);

        when(transferRepository.findByStatusAndScheduledForLessThanEqual(eq("SCHEDULED"), any(LocalDate.class)))
                .thenReturn(dueTransfers);

        scheduledTransferService.processDueTransfers();

        assertEquals("EXECUTED", transfer1.getStatus());
        assertNotNull(transfer1.getExecutedAt());

        assertEquals("EXECUTED", transfer2.getStatus());
        assertNotNull(transfer2.getExecutedAt());

        verify(transferRepository, times(2)).save(any(ScheduledTransfer.class));
    }
}
