package com.desafio.integrados.pix.repository;

import com.desafio.integrados.pix.domain.PixTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PixTransactionRepository extends JpaRepository<PixTransaction, Long> {
    List<PixTransaction> findBySenderIdOrReceiverIdOrderByCreatedAtDesc(Long senderId, Long receiverId);
}
