package com.desafio.integrados.pix.service;

import com.desafio.integrados.pix.domain.PixKey;
import com.desafio.integrados.pix.domain.PixTransaction;
import com.desafio.integrados.pix.dto.PixKeyRequest;
import com.desafio.integrados.pix.dto.PixKeyResponse;
import com.desafio.integrados.pix.dto.PixTransactionResponse;
import com.desafio.integrados.pix.dto.PixTransferRequest;
import com.desafio.integrados.pix.repository.PixKeyRepository;
import com.desafio.integrados.pix.repository.PixTransactionRepository;
import com.desafio.integrados.usuario.domain.User;
import com.desafio.integrados.usuario.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PixService {

    private final PixKeyRepository pixKeyRepository;
    private final PixTransactionRepository pixTransactionRepository;
    private final UserRepository userRepository;

    public PixService(PixKeyRepository pixKeyRepository, 
                      PixTransactionRepository pixTransactionRepository, 
                      UserRepository userRepository) {
        this.pixKeyRepository = pixKeyRepository;
        this.pixTransactionRepository = pixTransactionRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public PixKeyResponse registerKey(Long userId, PixKeyRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        if (pixKeyRepository.findByKeyValue(request.getKeyValue()).isPresent()) {
            throw new IllegalArgumentException("Esta chave Pix já está registrada.");
        }

        PixKey key = new PixKey(user, request.getKeyValue(), request.getKeyType());
        PixKey saved = pixKeyRepository.save(key);

        return new PixKeyResponse(saved.getId(), saved.getKeyValue(), saved.getKeyType());
    }

    public List<PixKeyResponse> listMyKeys(Long userId) {
        return pixKeyRepository.findByUserId(userId).stream()
                .map(k -> new PixKeyResponse(k.getId(), k.getKeyValue(), k.getKeyType()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void removeKey(Long userId, Long keyId) {
        PixKey key = pixKeyRepository.findById(keyId)
                .orElseThrow(() -> new IllegalArgumentException("Chave Pix não encontrada."));

        if (!key.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Você não tem permissão para remover esta chave.");
        }

        pixKeyRepository.delete(key);
    }

    @Transactional
    public PixTransactionResponse transfer(Long senderId, PixTransferRequest request) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Remetente não encontrado."));

        PixKey destination = pixKeyRepository.findByKeyValue(request.getDestinationKey())
                .orElseThrow(() -> new IllegalArgumentException("Chave Pix de destino não encontrada."));

        User receiver = destination.getUser();

        if (sender.getId().equals(receiver.getId())) {
            throw new IllegalArgumentException("Não é possível transferir para você mesmo.");
        }

        // Lógica simulada de saldo (já que a entidade User não tem saldo explícito no projeto atual)
        // Vamos apenas registrar a transação. Se houvesse saldo, seria validado aqui.

        String txid = UUID.randomUUID().toString().replace("-", "");

        PixTransaction tx = new PixTransaction(
                sender, 
                receiver, 
                sender.getCpf(), 
                receiver.getCpf(), 
                request.getAmount(), 
                request.getDescription(), 
                txid
        );

        PixTransaction savedTx = pixTransactionRepository.save(tx);

        return new PixTransactionResponse(
                savedTx.getTxid(),
                sender.getName(),
                receiver.getName(),
                savedTx.getAmount(),
                savedTx.getDescription(),
                savedTx.getStatus(),
                savedTx.getCreatedAt()
        );
    }

    public List<PixTransactionResponse> getMyTransactions(Long userId) {
        return pixTransactionRepository.findBySenderIdOrReceiverIdOrderByCreatedAtDesc(userId, userId).stream()
                .map(tx -> new PixTransactionResponse(
                        tx.getTxid(),
                        tx.getSender() != null ? tx.getSender().getName() : "Desconhecido",
                        tx.getReceiver() != null ? tx.getReceiver().getName() : "Desconhecido",
                        tx.getAmount(),
                        tx.getDescription(),
                        tx.getStatus(),
                        tx.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }
}
