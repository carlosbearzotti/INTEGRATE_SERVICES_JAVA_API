package com.desafio.integrados.criptografia.service;

import com.desafio.integrados.criptografia.domain.Transaction;
import com.desafio.integrados.criptografia.dto.TransactionDTO;
import com.desafio.integrados.criptografia.repository.TransactionRepository;
import com.desafio.integrados.usuario.domain.User;
import com.desafio.integrados.usuario.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository repository;
    private final UserRepository userRepository;

    public TransactionService(TransactionRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    public TransactionDTO create(TransactionDTO dto) {
        User user = null;
        if (dto.getUserId() != null) {
            user = userRepository.findById(dto.getUserId()).orElse(null);
        }

        Transaction entity = new Transaction(user, dto.getUserDocument(), dto.getCreditCardToken(), dto.getValue());
        entity = repository.save(entity);
        return mapToDTO(entity);
    }

    public TransactionDTO findById(Long id) {
        return repository.findById(id)
                .map(this::mapToDTO)
                .orElse(null);
    }

    public List<TransactionDTO> findAll() {
        return repository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<TransactionDTO> findByUserId(Long userId) {
        return repository.findByUserId(userId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public TransactionDTO update(Long id, TransactionDTO dto) {
        return repository.findById(id).map(existing -> {
            existing.setUserDocument(dto.getUserDocument());
            existing.setCreditCardToken(dto.getCreditCardToken());
            existing.setValue(dto.getValue());
            if (dto.getUserId() != null) {
                User user = userRepository.findById(dto.getUserId()).orElse(null);
                existing.setUser(user);
            }
            return mapToDTO(repository.save(existing));
        }).orElse(null);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    private TransactionDTO mapToDTO(Transaction entity) {
        Long userId = entity.getUser() != null ? entity.getUser().getId() : null;
        return new TransactionDTO(
                entity.getId(),
                userId,
                entity.getUserDocument(),
                entity.getCreditCardToken(),
                entity.getValue()
        );
    }
}
