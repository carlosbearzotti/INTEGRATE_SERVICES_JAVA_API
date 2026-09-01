package com.desafio.integrados.cartao.repository;

import com.desafio.integrados.cartao.domain.CardInvoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardInvoiceRepository extends JpaRepository<CardInvoice, Long> {
    List<CardInvoice> findByUserIdOrderByDueDateDesc(Long userId);
}
